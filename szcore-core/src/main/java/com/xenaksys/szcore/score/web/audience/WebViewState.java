package com.xenaksys.szcore.score.web.audience;

import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_SECTION_ACTIVE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SECTION_NAME;
import static com.xenaksys.szcore.Consts.WEB_OBJ_VIEW_STATE;

public class WebViewState {

    private final Set<String> activeViews = new HashSet<>();
    private String sectionName;
    private boolean isSectionActive;

    private final PropertyChangeSupport pcs;

    public WebViewState(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    public void activateView(String view) {
        activeViews.add(view);
    }

    public void activateViews(String... views) {
        for(String view : views) {
            activateView(view);
        }
    }

    public void deactivateView(String view) {
        activeViews.remove(view);
    }

    public void deactivateViews(String... views) {
        for(String view : views) {
            deactivateView(view);
        }
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
        pcs.firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_SECTION_NAME, this);
    }

    public boolean isSectionActive() {
        return isSectionActive;
    }

    public void setSectionActive(boolean sectionActive) {
        isSectionActive = sectionActive;
        pcs.firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_IS_SECTION_ACTIVE, this);
    }

    public Set<String> getActiveViews() {
        return activeViews;
    }

    public void deactivateAllViews() {
        activeViews.clear();
    }
}
