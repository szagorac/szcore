package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.WebAudienceScore;

public class WebElementStateExport {
    private String id;
    private boolean isActive;
    private boolean isPlaying;
    private boolean isPlayingNext;
    private boolean isPlayed;
    private boolean isVisible;
    private boolean isSelected;
    private int clickCount;

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPlayingNext() {
        return isPlayingNext;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void populate(WebAudienceScore.WebAudienceElementState from) {
        if (from == null) {
            return;
        }
        this.id = from.getId();
        this.isActive = from.isActive();
        this.isPlaying = from.isPlaying();
        this.isPlayingNext = from.isPlayingNext();
        this.isPlayed = from.isPlayed();
        this.isVisible = from.isVisible();
        this.isSelected = from.isSelected();
        this.clickCount = from.getClickCount();
    }

    @Override
    public String toString() {
        return "WebElementStateExport{" +
                "id='" + id + '\'' +
                ", isActive=" + isActive +
                ", isPlaying=" + isPlaying +
                ", isPlayingNext=" + isPlayingNext +
                ", isPlayed=" + isPlayed +
                ", isVisible=" + isVisible +
                ", isSelected=" + isSelected +
                ", clickCount=" + clickCount +
                '}';
    }
}
