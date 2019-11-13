package com.xenaksys.szcore.receive;

import java.util.List;

public interface SzcoreDistributorListener extends SzcoreIncomingEventListener {

    List<SzcoreIncomingEventListener> getListeners();

    void addListener(SzcoreIncomingEventListener listener);

    void removeListener(SzcoreIncomingEventListener listener);

}
