package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.PrepStaveChangeEvent;
import com.xenaksys.szcore.event.TempoChangeEvent;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempoModifierTask extends EventMusicTask {
    static final Logger LOG = LoggerFactory.getLogger(TempoModifierTask.class);

    private ScoreProcessorImpl scoreProcessor;

    public TempoModifierTask(long playTime, TempoChangeEvent event, ScoreProcessorImpl scoreProcessor) {
        super(playTime, event);
        this.scoreProcessor = scoreProcessor;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (event == null || !(event instanceof PrepStaveChangeEvent)) {
            return;
        }

        PrepStaveChangeEvent prepEvent = (PrepStaveChangeEvent) event;
        BeatId executeBeatId = event.getEventBaseBeat();
        Id instrumentId = executeBeatId.getInstrumentId();

//LOG.debug("### About to execute PrepStaveChangeTask beatid: " + executeBeatId);
        scoreProcessor.processPrepStaveChange(instrumentId, executeBeatId,
                prepEvent.getActivateBaseBeat(), prepEvent.getDeactivateBaseBeat(), prepEvent.getPageChangeOnBaseBeat(), prepEvent.getNextPageId());
    }
}
