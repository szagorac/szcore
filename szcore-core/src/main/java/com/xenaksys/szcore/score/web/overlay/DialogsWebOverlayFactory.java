package com.xenaksys.szcore.score.web.overlay;

public class DialogsWebOverlayFactory implements WebOverlayFactory {
    @Override
    public WebOverlayProcessor createOverlayProcessor() {
        return new DialogsWebOverlayProcessor();
    }
}
