package com.xenaksys.szcore.score;

public class WebElementState {
    private final String id;
    private boolean isActive;
    private boolean isPlaying;
    private boolean isPlayingNext;
    private boolean isPlayed;
    private boolean isVisible;
    private boolean isSelected;
    private int clickCount;

    public WebElementState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public void incrementClickCount() {
        this.clickCount ++;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isPlayingNext() {
        return isPlayingNext;
    }

    public void setPlayingNext(boolean playingNext) {
        isPlayingNext = playingNext;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public void copyTo(WebElementState other) {
        other.setClickCount(getClickCount());
        other.setActive(isActive());
        other.setVisible(isVisible());
        other.setPlaying(isPlaying());
        other.setPlayingNext(isPlayingNext());
        other.setPlayed(isPlayed());
    }

    @Override
    public String toString() {
        return "WebElementState{" +
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
