package com.xenaksys.szcore.gui.event;

import com.xenaksys.szcore.gui.processor.ClientEventProcessor;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientScoreEngineEventReceiver implements SzcoreEngineEventListener {
    static final Logger LOG = LoggerFactory.getLogger(ClientScoreEngineEventReceiver.class);

    private final ClientEventProcessor processor;
    private final ScoreService scoreService;

    public ClientScoreEngineEventReceiver(ClientEventProcessor processor, ScoreService scoreService) {
        this.processor = processor;
        this.scoreService = scoreService;
    }

    public void init(){
        scoreService.subscribe(this);
    }

    @Override
    public void onEvent(SzcoreEvent event) {
        if(event == null){
            return;
        }
        processor.process(event);
    }

    @Override
    public void onEvent(SzcoreEvent event, int beatNo, int tickNo) {
        if(event == null){
            return;
        }
        processor.process(event, beatNo, tickNo);
    }

    @Override
    public void onTransportBeatEvent(Id transportId, int beatNo, int baseBeatNo) {
        processor.processTransportBeatEvent(transportId, beatNo, baseBeatNo);
    }

    @Override
    public void onTransportTickEvent(Id transportId, int beatNo, int baseBeatNo, int tickNo) {
        processor.processTransportTickEvent(transportId, beatNo, baseBeatNo, tickNo);
    }

    @Override
    public void onTransportTempoChange(Id transportId, Tempo tempo) {
        processor.processTransportTempoChange(transportId, tempo);
    }

    @Override
    public void onTransportPositionChange(Id transportId, int beatNo) {
        processor.processTransportPositionChange(transportId, beatNo);
    }
}
