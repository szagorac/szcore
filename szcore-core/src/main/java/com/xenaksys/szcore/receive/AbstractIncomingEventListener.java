package com.xenaksys.szcore.receive;


import com.xenaksys.szcore.model.id.OscListenerId;

abstract public class AbstractIncomingEventListener implements SzcoreIncomingEventListener {

    private final OscListenerId listenerId;

    public AbstractIncomingEventListener(OscListenerId listenerId) {
        this.listenerId = listenerId;
    }

    @Override
    public OscListenerId getId() {
        return listenerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractIncomingEventListener)) return false;

        AbstractIncomingEventListener that = (AbstractIncomingEventListener) o;

        return listenerId.equals(that.listenerId);

    }

    @Override
    public int hashCode() {
        return listenerId.hashCode();
    }

    @Override
    public String toString() {
        return "SzcoreListener{" +
                "listenerId=" + listenerId +
                '}';
    }
}
