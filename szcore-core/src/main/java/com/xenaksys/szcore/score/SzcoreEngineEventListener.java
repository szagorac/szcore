package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;

public interface SzcoreEngineEventListener {

    void onEvent(SzcoreEvent event);

    void onEvent(SzcoreEvent event, int beatNo, int tickNo);

    void onTransportBeatEvent(Id transportId, int beatNo, int baseBeatNo);

    void onTransportTickEvent(Id transportId, int beatNo, int baseBeatNo, int tickNo);

    void onTransportTempoChange(Id transportId, Tempo tempo);

    void onTransportPositionChange(Id transportId, int beatNo);
}
