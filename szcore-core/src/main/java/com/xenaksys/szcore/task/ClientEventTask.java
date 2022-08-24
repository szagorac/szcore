package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.gui.ClientEvent;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.SzcoreEvent;

public class ClientEventTask extends EventMusicTask {
    private ScoreProcessor scoreProcessor;

    public ClientEventTask(long playTime, ClientEvent event, ScoreProcessor scoreProcessor) {
        super(playTime, event);
        this.scoreProcessor = scoreProcessor;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof ClientEvent)) {
            return;
        }

        ClientEvent clientEvent = (ClientEvent) event;
        scoreProcessor.sendClientEvent(clientEvent);
    }
}
