package com.xenaksys.szcore.algo.config;

public class TextElementConfig {
    private final double dx;
    private final double dy;
    private final String txt;

    private double xAdj;
    private double yAdj;

    public TextElementConfig(double dx, double dy, String txt) {
        this.dx = dx;
        this.dy = dy;
        this.txt = txt;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public String getTxt() {
        return txt;
    }

    public double getXAdj() {
        return xAdj;
    }

    public void setXAdj(double xAdj) {
        this.xAdj = xAdj;
    }

    public double getYAdj() {
        return yAdj;
    }

    public void setYAdj(double yAdj) {
        this.yAdj = yAdj;
    }

    @Override
    public String toString() {
        return "TextElementConfig{" +
                "dx=" + dx +
                ", dy=" + dy +
                ", txt='" + txt + '\'' +
                '}';
    }
}
