package com.xenaksys.szcore.web;

public interface WebScoreStateListener {

    void onWebScoreStateChange(WebScoreState webScoreState);

    void onWebScoreStateDeltaChange(WebScoreStateDelta webScoreStateDelta);

}
