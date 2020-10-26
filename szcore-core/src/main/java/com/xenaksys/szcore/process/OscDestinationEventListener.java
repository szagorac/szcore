package com.xenaksys.szcore.process;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.OscScriptEvent;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.id.BarId;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.score.InstrumentBeatTracker;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.xenaksys.szcore.Consts.OSC_CMD_SET_TEMPO;
import static com.xenaksys.szcore.Consts.OSC_UPDATE_BEAT_COMPLETE_PERC_THRESHOLD;

public class OscDestinationEventListener implements SzcoreEngineEventListener {
    static final Logger LOG = LoggerFactory.getLogger(OscDestinationEventListener.class);

    static final String ADDR_BEAT_INFO = Consts.OSC_ADDR_BEAT_INFO;
    static final String OSC_CMD_BEAT_INFO = Consts.OSC_CMD_BEAT_INFO;

    private final ScoreProcessorImpl processor;
    private final EventFactory eventFactory;
    private final Clock clock;

    private CopyOnWriteArrayList<Instrument> destinations = new CopyOnWriteArrayList<>();

    public OscDestinationEventListener(ScoreProcessorImpl processor, EventFactory eventFactory, Clock clock) {
        this.processor = processor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        init();
    }

    public void init() {
        processor.subscribe(this);
    }

    @Override
    public void onEvent(SzcoreEvent event) {
        //do nothing, would cause loop
    }

    @Override
    public void onEvent(SzcoreEvent event, int beatNo, int tickNo) {
        //do nothing, would cause loop
    }

    @Override
    public void onTransportBeatEvent(Id transportId, int beatNo, int baseBeatNo) {
        sendBeatInfo(beatNo, false);
    }

    @Override
    public void onTransportTickEvent(Id transportId, int beatNo, int baseBeatNo, int tickNo) {

    }

    @Override
    public void onTransportTempoChange(Id transportId, Tempo tempo) {
        // if scheduler running tempo event sent my events
        if (processor.isSchedulerRunning()) {
            return;
        }
        if (destinations.isEmpty()) {
            return;
        }

        for (Instrument destination : destinations) {
            InstrumentBeatTracker instrumentBeatTracker = processor.getInstrumentBeatTracker(destination.getId());
            Beat currentBeat = instrumentBeatTracker.getCurrent();
            BeatId beatId = currentBeat.getBeatId();
            List<Object> args = new ArrayList<>(2);
            //arg0 command
            args.add(OSC_CMD_SET_TEMPO);
            args.add(tempo.getBpm());
            args.add(tempo.getTempoModifier().getMultiplier());

            LOG.info("onTransportTempoChange: Sending tempo change bpm: {}", tempo.getBpm());
            OscScriptEvent oscScriptEvent = eventFactory.createOscScriptEvent(destination.getName(), beatId, ADDR_BEAT_INFO, args, clock.getSystemTimeMillis());
            processor.publishOscEvent(oscScriptEvent);
        }
    }

    @Override
    public void onTransportPositionChange(Id transportId, int beatNo) {
        sendBeatInfo(beatNo, true);
    }

    private void sendBeatInfo(int beatNo, boolean isSendNow) {
        if (destinations.isEmpty()) {
            return;
        }

        for (Instrument destination : destinations) {
            InstrumentBeatTracker instrumentBeatTracker = processor.getInstrumentBeatTracker(destination.getId());
            Beat currentBeat = instrumentBeatTracker.getCurrent();
            int percCompleted = instrumentBeatTracker.getPercentCompleted();
            if (percCompleted > OSC_UPDATE_BEAT_COMPLETE_PERC_THRESHOLD) {
//                LOG.debug("onTransportBeatEvent: not sending beat info update because beat percCompleted: {}", percCompleted);
                continue;
            }

            BeatId beatId = currentBeat.getBeatId();
            int bbno = beatId.getBeatNo();
            if (bbno != beatNo) {
                LOG.warn("onTransportBeatEvent: retrieved beatNo: {} not equal to expected  beatNo: {}", bbno, beatNo);
                beatNo = bbno;
            }

            PageId pageId = (PageId) beatId.getPageId();
            int pageNo = pageId.getPageNo();
            BarId barId = (BarId) beatId.getBarId();
            int barNo = barId.getBarNo();

            List<Object> args = new ArrayList<>(4);
            //arg0 command
            args.add(OSC_CMD_BEAT_INFO);
            args.add(pageNo);
            args.add(barNo);
            args.add(beatNo);
            OscScriptEvent oscScriptEvent = eventFactory.createOscScriptEvent(destination.getName(), beatId, ADDR_BEAT_INFO, args, clock.getSystemTimeMillis());
            if (isSendNow) {
                processor.publishOscEvent(oscScriptEvent);
            } else {
                processor.addBeatEventToProcess(oscScriptEvent);
            }
        }
    }

    public void reloadDestinations(Collection<Instrument> maxClients) {
        destinations.clear();
        for (Instrument maxClient : maxClients) {
            addDestination(maxClient);
        }
    }

    public void addDestination(Instrument destination) {
        if (destination == null) {
            return;
        }
        destinations.add(destination);
    }
}