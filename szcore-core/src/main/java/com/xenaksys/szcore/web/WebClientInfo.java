package com.xenaksys.szcore.web;

import gnu.trove.stack.array.TLongArrayStack;

public class WebClientInfo {
    private final String clientAddr;

    private WebConnectionType connectionType;
    private String userAgent;
    private TLongArrayStack latencies = new TLongArrayStack(100);

    public WebClientInfo(String clientAddr) {
        this.clientAddr = clientAddr;
    }

    public WebConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(WebConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getClientAddr() {
        return clientAddr;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public WebConnection getConnection() {
        return new WebConnection(clientAddr, connectionType);
    }

    public long getLatency() {
        long[] ls = latencies.toArray();
        long sum = 0L;
        int counter = 0;
        for(long l : ls) {
            if(sum + l >= Long.MAX_VALUE) {
                break;
            }
            sum += l;
            counter++;
        }
        long latency = 0L;
        if(counter > 0) {
            latency = sum / counter;
        }
        return latency;
    }

    public void addLatency(long latency) {
        if(latencies.size() >= 100) {
            latencies.pop();
        }
        this.latencies.push(latency);
    }

}
