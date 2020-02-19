package com.xenaksys.szcore.web;

import com.xenaksys.szcore.event.OutgoingWebEvent;

public interface WebScoreEventListener {

    void onWebScoreEvent(WebScoreState webScoreState);

    void onOutgoingWebEvent(OutgoingWebEvent webEvent);

}
