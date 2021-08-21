package com.xenaksys.szcore.score.web.audience;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.WEB_OBJ_ELEMENT_STATE;

public class WebAudienceElementState {
    private final String id;
    private final PropertyChangeSupport pcs;

    private boolean isActive;
    private boolean isPlaying;
    private boolean isPlayingNext;
    private boolean isPlayed;
    private boolean isVisible;
    private boolean isSelected;
    private int clickCount;

    public WebAudienceElementState(String id, PropertyChangeSupport pcs) {
        this.id = id;
        this.pcs = pcs;
    }

    public String getId() {
        return id;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        int old = this.clickCount;
        this.clickCount = clickCount;
        if (old != this.clickCount) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }
    }

    public void incrementClickCount() {
        this.clickCount++;
        pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
    }

    public void decrementClickCount() {
        this.clickCount--;
        pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        boolean old = this.isActive;
        this.isActive = active;
        if (old != this.isActive) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        boolean old = this.isVisible;
        this.isVisible = visible;
        if (old != this.isVisible) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        boolean old = this.isPlaying;
        this.isPlaying = playing;
        if (old != this.isPlaying) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        boolean old = this.isSelected;
        this.isSelected = selected;
        if (old != this.isSelected) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }
    }

    public boolean isPlayingNext() {
        return isPlayingNext;
    }

    public void setPlayingNext(boolean playingNext) {
        boolean old = this.isPlayingNext;
        this.isPlayingNext = playingNext;
        if (old != this.isPlayingNext) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        boolean old = this.isPlayed;
        this.isPlayed = played;
        if (old != this.isPlayed) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }
    }

    public void copyTo(WebAudienceElementState other) {
        other.setClickCount(getClickCount());
        other.setActive(isActive());
        other.setVisible(isVisible());
        other.setPlaying(isPlaying());
        other.setPlayingNext(isPlayingNext());
        other.setPlayed(isPlayed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebAudienceElementState that = (WebAudienceElementState) o;
        return isActive == that.isActive && isPlaying == that.isPlaying && isPlayingNext == that.isPlayingNext && isPlayed == that.isPlayed && isVisible == that.isVisible && isSelected == that.isSelected && clickCount == that.clickCount && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isActive, isPlaying, isPlayingNext, isPlayed, isVisible, isSelected, clickCount);
    }

    @Override
    public String toString() {
        return "WebAudienceElementState{" +
                "id='" + id + '\'' +
                ", isActive=" + isActive +
                ", isPlaying=" + isPlaying +
                ", isPlayingNext=" + isPlayingNext +
                ", isPlayed=" + isPlayed +
                ", isVisible=" + isVisible +
                ", clickCount=" + clickCount +
                '}';
    }
}
