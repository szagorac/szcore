package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.OscEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScriptEventPreset {
    private final int id;
    private final List<OscEvent> scriptEvents = new ArrayList<>();

    public ScriptEventPreset(int id) {
        this.id = id;
    }

    public void addScriptEvents(List<OscEvent> events) {
        if (events == null) {
            return;
        }
        this.scriptEvents.addAll(events);
    }

    public void addScriptEvent(OscEvent scriptEvent) {
        if (scriptEvent == null) {
            return;
        }
        scriptEvents.add(scriptEvent);
    }

    public List<OscEvent> getScriptEvents() {
        return scriptEvents;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptEventPreset that = (ScriptEventPreset) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ScriptPreset{" +
                "id=" + id +
                ", scripts=" + scriptEvents +
                '}';
    }
}
