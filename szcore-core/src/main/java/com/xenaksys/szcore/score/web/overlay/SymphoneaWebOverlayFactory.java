package com.xenaksys.szcore.score.web.overlay;

public class SymphoneaWebOverlayFactory implements WebOverlayFactory {
    @Override
    public WebOverlayProcessor createOverlayProcessor() {
        return new SymphoneaWebOverlayProcessor();
    }
}
