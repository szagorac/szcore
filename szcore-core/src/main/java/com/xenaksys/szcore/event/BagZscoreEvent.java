package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.EnumMap;
import java.util.Set;

public class BagZscoreEvent implements SzcoreEvent {

    private final EnumMap<ZscoreEventParam, Object> params;
    private EventType type;

    public BagZscoreEvent() {
        this.params = new EnumMap<>(ZscoreEventParam.class);
        this.type = EventType.UNKNOWN;
    }

    public void reset() {
        this.params.clear();
        this.type = EventType.UNKNOWN;
    }

    public void addParam(ZscoreEventParam type, Object value) {
        params.put(type, value);
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Set<ZscoreEventParam> getParamTypes() {
        return params.keySet();
    }

    public Object getParam(ZscoreEventParam type) {
        return params.get(type);
    }

    public Object removeParam(ZscoreEventParam type) {
        return params.remove(type);
    }

    @Override
    public EventType getEventType() {
        return type;
    }

    @Override
    public BeatId getEventBaseBeat() {
        Object bid = getValue(ZscoreEventParam.EVENT_PARAM_BEAT_ID, BeatId.class);
        return (bid == null)?null:(BeatId)bid;
    }

    @Override
    public long getCreationTime() {
        Object time = getValue(ZscoreEventParam.EVENT_PARAM_CREATION_TIME, Long.class);
        return (time == null)?0L:(Long)time;
    }

    @Override
    public String toString() {
        return "BagSysEvent{" +
                " type=" + type +
                ", params=" + params +
                '}';
    }

    public Object getValue(ZscoreEventParam param, Class clazz) {
        Object bid = getParam(param);
        if (!validate(bid, clazz)) {
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


