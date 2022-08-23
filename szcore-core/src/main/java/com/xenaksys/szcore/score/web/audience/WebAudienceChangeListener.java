package com.xenaksys.szcore.score.web.audience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WebAudienceChangeListener implements PropertyChangeListener {
    static final Logger LOG = LoggerFactory.getLogger(WebAudienceChangeListener.class);

    private final WebAudienceStateDeltaTracker stateDeltaTracker;

    public WebAudienceChangeListener(WebAudienceStateDeltaTracker stateDeltaTracker) {
        this.stateDeltaTracker = stateDeltaTracker;
    }

    public void propertyChange(PropertyChangeEvent changeEvent) {
        if (changeEvent == null) {
            return;
        }
        Object idObj = changeEvent.getOldValue();
        if (!(idObj instanceof String)) {
            LOG.error("propertyChange: invalid object id type, expected String");
            return;
        }
        String objId = (String) idObj;
        stateDeltaTracker.processUpdate(changeEvent.getPropertyName(), objId, changeEvent.getNewValue());
    }
}
