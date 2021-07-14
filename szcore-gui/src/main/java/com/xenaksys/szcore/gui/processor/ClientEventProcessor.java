package com.xenaksys.szcore.gui.processor;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.ClientEvent;
import com.xenaksys.szcore.event.ClientEventType;
import com.xenaksys.szcore.event.ErrorEvent;
import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.event.IncomingOscEvent;
import com.xenaksys.szcore.event.InstrumentEvent;
import com.xenaksys.szcore.event.MusicEvent;
import com.xenaksys.szcore.event.MusicEventType;
import com.xenaksys.szcore.event.OscEvent;
import com.xenaksys.szcore.event.OscEventType;
import com.xenaksys.szcore.event.ParticipantEvent;
import com.xenaksys.szcore.event.ParticipantStatsEvent;
import com.xenaksys.szcore.event.StopEvent;
import com.xenaksys.szcore.event.TempoChangeEvent;
import com.xenaksys.szcore.event.TimeSigChangeEvent;
import com.xenaksys.szcore.event.WebAudienceEvent;
import com.xenaksys.szcore.event.WebClientInfoUpdateEvent;
import com.xenaksys.szcore.event.WebScoreInEvent;
import com.xenaksys.szcore.gui.SzcoreClient;
import com.xenaksys.szcore.gui.model.Participant;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Processor;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.util.IpAddressValidator;
import com.xenaksys.szcore.util.TimeUtil;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEventProcessor implements Processor {
    static final Logger LOG = LoggerFactory.getLogger(ClientEventProcessor.class);

    private final SzcoreClient client;

    private final IpAddressValidator ipValidator = new IpAddressValidator();

    public ClientEventProcessor(SzcoreClient client) {
        this.client = client;
     }

    private volatile int currentBeatNo;

    @Override
    public void process(SzcoreEvent event) {
        if(event == null){
            return;
        }

        client.logEvent(event);

        EventType type = event.getEventType();
        if(type == null){
            return;
        }

        switch (type){
            case OSC:
                if((event instanceof IncomingOscEvent)){
                    processIncomingOscEvent((IncomingOscEvent)event);
                } else {
                    processScoreOscEvent((OscEvent) event);
                }
                break;
            case CLIENT:
                if ((event instanceof ClientEvent)) {
                    processClientEvent((ClientEvent) event);
                }
                break;
            case MUSIC:
                if ((event instanceof MusicEvent)) {
                    processMusicEvent((MusicEvent) event);
                }
                break;
            case WEB_AUDIENCE:
                if ((event instanceof WebAudienceEvent)) {
                    processWebScoreEvent((WebAudienceEvent) event);
                }
                break;
            case WEB_SCORE_IN:
                processWebScoreInEvent((WebScoreInEvent) event);
                break;
            case SCRIPTING_ENGINE:
                //TODO
                break;
            default:
                LOG.error("process event: Unknown event type: " + type);
        }
    }

    public void process(SzcoreEvent event, int beatNo, int tickNo) {
        if(event == null){
            return;
        }

        client.logEvent(event);

        EventType type = event.getEventType();
        if(type == null){
            return;
        }

        switch (type) {
            case OSC:
                processScoreOscEvent((OscEvent) event, beatNo, tickNo);
                break;
            case MUSIC:
                processMusicEvent((MusicEvent) event, beatNo, tickNo);
                break;
            case WEB_AUDIENCE:
                processWebScoreEvent((WebAudienceEvent) event, beatNo, tickNo);
                break;
            case SCRIPTING_ENGINE:
                //TODO
                break;
            default:
                LOG.error("process beat event: Unknown event type: " + type);
        }
    }

    private void processMusicEvent(MusicEvent event, int beatNo, int tickNo) {
        MusicEventType type = event.getMusicEventType();
        switch (type) {
            case TEMPO_CHANGE:
                TempoChangeEvent tempoChangeEvent = (TempoChangeEvent) event;
                break;
            case TIMESIG_CHANGE:
                TimeSigChangeEvent timeSigChangeEvent = (TimeSigChangeEvent) event;
                break;
            case STOP:
                StopEvent stopEvent = (StopEvent)event;
                BeatId beatId = event.getEventBaseBeat();
                break;
            default:
                //

        }
    }

    private void processWebScoreEvent(WebAudienceEvent event, int beatNo, int tickNo) {

    }

    private void processScoreOscEvent(OscEvent event, int beatNo, int tickNo) {
        OscEventType type = event.getOscEventType();
        switch (type) {
            case DATE_TICK:
                break;
            default:
               //
        }
    }

    public void processTransportBeatEvent(Id transportId, int beatNo, int baseBeatNo) {
        client.onTransportBeatEvent(transportId, beatNo, baseBeatNo);
    }

    public void processTransportTempoChange(Id transportId, Tempo tempo) {
        client.onTempoEvent(transportId, tempo);
    }


    public void processTransportPositionChange(Id transportId, int beatNo) {
        client.onTransportPositionChange(transportId, beatNo);
    }

    public void processTransportTickEvent(Id transportId, int beatNo, int baseBeatNo, int tickNo) {

    }

    private void processMusicEvent(MusicEvent event) {
        LOG.debug("Received score MUSIC event: " + event);
    }

    private void processWebScoreEvent(WebAudienceEvent event) {
        LOG.debug("Received WebAudienceScore MUSIC event: " + event);
    }

    private void processWebScoreInEvent(WebScoreInEvent event) {
        LOG.debug("Received WebScoreInEvent MUSIC event: " + event);
    }

    private void processScoreOscEvent(OscEvent event) {
        LOG.debug("Received score OSC event: " + event);
    }

    private void processClientEvent(ClientEvent event) {
        ClientEventType type = event.getClientEventType();
        switch (type) {
            case PARTICIPANT:
                processParticipantEvent((ParticipantEvent) event);
                break;
            case PARTICIPANT_STATS:
                processParticipantStatsEvent((ParticipantStatsEvent) event);
                break;
            case INSTRUMENT:
                processInstrumentEvent((InstrumentEvent) event);
                break;
            case ERROR:
                processErrorEvent((ErrorEvent) event);
                break;
            case WEB_CLIENT_INFOS:
                processWebClientInfoEvent((WebClientInfoUpdateEvent) event);
                break;
            default:
                LOG.error("processClientEvent: Unknown event type: " + type);
        }
    }

    private void processWebClientInfoEvent(WebClientInfoUpdateEvent event) {
        if (event == null) {
            return;
        }
        client.processWebClientInfos(event);
    }

    private void processErrorEvent(ErrorEvent event) {
        if (event == null) {
            return;
        }

        LOG.error(event.getExceptionMessage());
        client.showDialog(event.getSource() + " Error", Alert.AlertType.ERROR, event.getError(), event.getExceptionMessage());
    }

    private void processIncomingOscEvent(IncomingOscEvent event) {
        String address = event.getAddress();
        switch(address){
            case Consts.SZCORE_ADDR:
                processSzcoreMessage(event);
                break;
            case Consts.INSCORE_ADDR:
                processInscoreMessage(event);
                break;
            case Consts.OSC_INSCORE_ADDRESS_ROOT:
                processAppMessage(event);
                break;
            case Consts.ERR_ADDR:
                processError(event);
                break;
            default:
                LOG.error("Unknown address: " + address);
        }
    }

    private void processAppMessage(IncomingOscEvent event) {
        LOG.debug("Received APP message: " + event.getAddress() + " args: " + event.getArguments());
    }

    private void processInscoreMessage(IncomingOscEvent event) {
        LOG.debug("Received Inscore message: " + event.getAddress() + " args: " + event.getArguments());
    }

    private void processSzcoreMessage(IncomingOscEvent event) {
//        LOG.debug("Received SZCORE message: " + event.getAddress() + " args: " + event.getArguments());
    }

    private void processError(IncomingOscEvent event) {
        LOG.debug("Received ERROR message: " + event.getAddress() + " args: " + event.getArguments());
    }

    private void processParticipantEvent(ParticipantEvent event) {
        if (event == null){
            return;
        }

        Participant participant = new Participant();
        participant.setInetAddress(event.getInetAddress());
        participant.setHostAddress(event.getHostAddress());
        participant.setPortIn(event.getPortIn());
        participant.setPortOut(event.getPortOut());
        participant.setPortErr(event.getPortErr());
        participant.setPing(event.getPing());
        participant.setInstrument(event.getInstrument());

        client.addParticipant(participant);
    }

    private void processParticipantStatsEvent(ParticipantStatsEvent event) {
        if (event == null){
            return;
        }

        Participant participant = client.getParticipant(event.getHostAddress(), event.getPort());
        if (participant == null) {
            LOG.error("Can not find participant for event: " + event);
            return;
        }

        participant.setPing(event.getOneWayPingLatencyMillis());
        participant.setExpired(event.isExpired());
        String pingPeriod = TimeUtil.formatPeriod(event.getLastPingMillis());
        participant.setLastPingTime(pingPeriod);
    }

    private void processInstrumentEvent(InstrumentEvent event) {

        String hostAddress = event.getHostAddress();
        Participant participant = client.getParticipant(hostAddress, event.getPort());
        if(participant == null){
            LOG.error("Failed to find participant for event: " + event);
            return;
        }

        String instrument = event.getInstrument();
        participant.setInstrument(instrument);

    }

}
