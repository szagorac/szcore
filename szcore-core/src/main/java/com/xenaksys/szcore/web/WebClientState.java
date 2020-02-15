package com.xenaksys.szcore.web;

import gnu.trove.stack.array.TLongArrayStack;

public class WebClientState {
    private final String clientAddr;
    private TLongArrayStack latencies = new TLongArrayStack(100);

    public WebClientState(String clientAddr) {
        this.clientAddr = clientAddr;
    }

    public String getClientAddr() {
        return clientAddr;
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
