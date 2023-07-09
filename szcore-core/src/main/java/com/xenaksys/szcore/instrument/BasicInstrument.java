package com.xenaksys.szcore.instrument;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.id.InstrumentId;

public class BasicInstrument implements Instrument {

    private final Id id;
    private final String name;
    private final boolean isAv;

    public BasicInstrument(Id id, String name, boolean isAv) {
        this.name = name;
        this.id = id;
        this.isAv = isAv;
    }

    @Override
    public Id getId() {
        return id;
    }

    public InstrumentId getInstrumentId() {
        return (InstrumentId)id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isAv() {
        return this.isAv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicInstrument)) return false;

        BasicInstrument that = (BasicInstrument) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "BasicInstrument{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
