package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.EnumMap;
import java.util.Set;

public class BagSzcoreEvent implements SzcoreEvent {

    private final EnumMap<SzcoreEventParam, Object> params;
    private EventType type;

    public BagSzcoreEvent() {
        this.params = new EnumMap<>(SzcoreEventParam.class);
        this.type = EventType.UNKNOWN;
    }

    public void reset() {
        this.params.clear();
        this.type = EventType.UNKNOWN;
    }

    public void addParam(SzcoreEventParam type, Object value) {
        params.put(type, value);
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Set<SzcoreEventParam> getParamTypes() {
        return params.keySet();
    }

    public Object getParam(SzcoreEventParam type) {
        return params.get(type);
    }

    public Object removeParam(SzcoreEventParam type) {
        return params.remove(type);
    }

    @Override
    public EventType getEventType() {
        return type;
    }

    @Override
    public BeatId getEventBaseBeat() {
        Object bid = getValue(SzcoreEventParam.EVENT_PARAM_BEAT_ID, BeatId.class);
        return (bid == null)?null:(BeatId)bid;
    }

    @Override
    public long getCreationTime() {
        Object time = getValue(SzcoreEventParam.EVENT_PARAM_CREATION_TIME, Long.class);
        return (time == null)?0L:(Long)time;
    }

    @Override
    public String toString() {
        return "BagSysEvent{" +
                " type=" + type +
                ", params=" + params +
                '}';
    }

    public Object getValue(SzcoreEventParam param, Class clazz) {
        Object bid = getParam(param);
        if(!validate(bid, clazz)){
            return null;
        }
        return bid;
    }

    protected boolean validate(Object obj, Class clazz) {
        if(obj == null){
            return false;
        }
        return clazz.isAssignableFrom(obj.getClass());
    }
}


