package com.xenaksys.szcore.score.web.overlay;

public class DefaultWebOverlayFactory implements WebOverlayFactory {
    @Override
    public WebOverlayProcessor createOverlayProcessor() {
        return new WebOverlayProcessor();
    }
}
