package com.xenaksys.szcore.event.music;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.id.BeatId;

public class ScoreSectionEvent extends MusicEvent {

    private final Id transportId;
    private final String section;
    private final ScoreSectionEventType sectionEventType;

    public ScoreSectionEvent(BeatId beatId, String section, ScoreSectionEventType sectionEventType, Id transportId, long creationTime) {
        super(beatId, creationTime);
        this.section = section;
        this.sectionEventType = sectionEventType;
        this.transportId = transportId;
    }

    public Id getTransportId() {
        return transportId;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.SCORE_SECTION;
    }

    public ScoreSectionEventType getSectionEventType() {
        return sectionEventType;
    }

    public String getSection() {
        return section;
    }

    @Override
    public String toString() {
        return "ScoreSectionEvent{" +
                "beatId=" + getEventBaseBeat().getBeatNo() +
                ", section=" + section +
                ", sectionEventType=" + sectionEventType +
                ", transportId=" + transportId +
                '}';
    }
}
