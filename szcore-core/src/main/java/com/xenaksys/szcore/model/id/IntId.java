package com.xenaksys.szcore.model.id;


import com.xenaksys.szcore.model.Id;

public class IntId implements Id {
    private final int id;

    public IntId(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntId)) return false;

        IntId intId = (IntId) o;

        return id == intId.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "IntId{" +
                "id=" + id +
                '}';
    }
}
