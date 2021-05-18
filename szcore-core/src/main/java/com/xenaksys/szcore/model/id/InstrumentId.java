package com.xenaksys.szcore.model.id;

public class InstrumentId extends StrId implements Comparable<InstrumentId> {
    public InstrumentId(String id) {
        super(id);
    }

    @Override
    public int compareTo(InstrumentId o) {
        return this.getName().compareTo(o.getName());
    }
}
