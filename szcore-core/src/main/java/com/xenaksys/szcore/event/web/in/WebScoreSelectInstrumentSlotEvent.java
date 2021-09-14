package com.xenaksys.szcore.event.web.in;

public class WebScoreSelectInstrumentSlotEvent extends WebScoreInEvent {
    private final String part;
    private final int slotNo;
    private final String slotInstrument;

    public WebScoreSelectInstrumentSlotEvent(String clientId, String eventId, String sourceAddr, String part, int slotNo, String slotInstrument, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.part = part;
        this.slotNo = slotNo;
        this.slotInstrument = slotInstrument;
    }

    public String getPart() {
        return part;
    }

    public int getSlotNo() {
        return slotNo;
    }

    public String getSlotInstrument() {
        return slotInstrument;
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.SELECT_ISLOT;
    }

    @Override
    public String toString() {
        return "WebScoreSelectInstrumentSlotEvent{" +
                "part=" + part +
                '}';
    }
}
