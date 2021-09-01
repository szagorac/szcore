package com.xenaksys.szcore.score.web.audience.export.delegate;

import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.web.WebAudienceAction;

import java.util.List;

public class DialogsWebAudienceExport implements WebAudienceScoreStateExport {
    private final List<WebAudienceAction> actions;

    public DialogsWebAudienceExport(List<WebAudienceAction> currentActions) {
        this.actions = currentActions;
    }

    public List<WebAudienceAction> getActions() {
        return actions;
    }
}
