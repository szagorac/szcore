package com.xenaksys.szcore.score.web.strategy;

import com.xenaksys.szcore.score.web.WebMovementInfo;

public class WebDynamicScoreStrategy extends WebStrategy {
    private WebMovementInfo currentMovement;

    public WebMovementInfo getCurrentMovement() {
        return currentMovement;
    }

    public void setCurrentMovement(WebMovementInfo currentMovement) {
        this.currentMovement = currentMovement;
    }
}
