package com.xenaksys.szcore.score.web;

public class WebTextinfo {
    private final double x;
    private final double y;
    private final String txt;

    public WebTextinfo(double x, double y, String txt) {
        this.x = x;
        this.y = y;
        this.txt = txt;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getTxt() {
        return txt;
    }

    @Override
    public String toString() {
        return "WebTextinfo{" +
                "x=" + x +
                ", y=" + y +
                ", txt='" + txt + '\'' +
                '}';
    }
}
