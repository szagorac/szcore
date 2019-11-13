package com.xenaksys.szcore.model.id;


import com.xenaksys.szcore.model.Id;

public class StrId implements Id {
    private final String id;

    public StrId(String id) {
        this.id = id;
    }

    public String getName() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StrId)) return false;

        StrId strId = (StrId) o;

        return id.equals(strId.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "StrId{" +
                "id='" + id + '\'' +
                '}';
    }
}
