package com.xenaksys.szcore.model;

import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;

public interface EventReceiver extends Processor {

    void start();

    void addListener(SzcoreIncomingEventListener listener);

    void notifyListeners(SzcoreEvent event);
}
