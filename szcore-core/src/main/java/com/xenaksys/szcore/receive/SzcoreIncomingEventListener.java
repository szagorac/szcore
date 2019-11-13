package com.xenaksys.szcore.receive;

import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.OscListenerId;

public interface SzcoreIncomingEventListener {

    void onEvent(SzcoreEvent event);

    OscListenerId getId();

}
