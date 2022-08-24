package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.WebViewState;

import java.util.ArrayList;

public class WebViewStateExport {

    private ArrayList<String> activeViews;
    private String sectionName;
    private boolean isSectionActive;

    public void populate(WebViewState from) {
        if (from == null) {
            return;
        }
        this.activeViews = new ArrayList<>(from.getActiveViews());
        this.sectionName = from.getSectionName();
        this.isSectionActive = from.isSectionActive();
    }

    public ArrayList<String> getActiveViews() {
        return activeViews;
    }

    public String getSectionName() {
        return sectionName;
    }

    public boolean isSectionActive() {
        return isSectionActive;
    }

    @Override
    public String toString() {
        return "WebViewStateExport{" +
                "activeViews=" + activeViews +
                ", sectionName='" + sectionName + '\'' +
                ", isSectionActive=" + isSectionActive +
                '}';
    }
}
