package com.xenaksys.szcore.score;

public class WebElementState {
    private final String id;
    private boolean isActive;
    private boolean isPlayed;
    private boolean isVisible;
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

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    @Override
    public String toString() {
        return "WebElementState{" +
                "id='" + id + '\'' +
                ", isActive=" + isActive +
                ", isPlayed=" + isPlayed +
                ", isVisible=" + isVisible +
                ", clickCount=" + clickCount +
                '}';
    }
}
