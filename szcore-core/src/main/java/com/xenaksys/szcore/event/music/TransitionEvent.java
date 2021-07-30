package com.xenaksys.szcore.event.music;

import com.xenaksys.szcore.model.Transition;
import com.xenaksys.szcore.model.id.BeatId;


public class TransitionEvent extends MusicEvent {

    private final String destination;
    private final Transition transition;

    public TransitionEvent(BeatId beatId, String destination, Transition transition, long creationTime) {
        super(beatId, creationTime);
        this.destination = destination;
        this.transition = transition;
    }

    public String getDestination() {
        return destination;
    }

    public Transition getTransition() {
        return transition;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.TRANSITION;
    }

}
