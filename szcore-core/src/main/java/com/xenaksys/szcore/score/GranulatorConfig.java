package com.xenaksys.szcore.score;

public class GranulatorConfig {
    private GrainConfig grain;
    private EnvelopeConfig envelope;
    private PannerConfig panner;

    public GrainConfig getGrain() {
        return grain;
    }

    public void setGrain(GrainConfig grain) {
        this.grain = grain;
    }

    public EnvelopeConfig getEnvelope() {
        return envelope;
    }

    public void setEnvelope(EnvelopeConfig envelope) {
        this.envelope = envelope;
    }

    public PannerConfig getPanner() {
        return panner;
    }

    public void setPanner(PannerConfig panner) {
        this.panner = panner;
    }

    public boolean validate() {
        return grain.validate() && envelope.validate() && panner.validate();
    }

    @Override
    public String toString() {
        return "GranulatorConfig{" +
                "grainConfig=" + grain +
                ", envelopeConfig=" + envelope +
                ", pannerConfig=" + panner +
                '}';
    }
}
