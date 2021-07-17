package com.xenaksys.szcore.web;

import com.xenaksys.szcore.net.browser.BrowserOS;
import com.xenaksys.szcore.net.browser.BrowserType;
import com.xenaksys.szcore.net.browser.UAgentInfo;
import com.xenaksys.szcore.util.Histogram;
import gnu.trove.stack.array.TLongArrayStack;

import java.util.Objects;

import static com.xenaksys.szcore.Consts.EMPTY;

public class WebClientInfo {
    private final String clientAddr;

    private WebConnection webConnection = new WebConnection(EMPTY, WebConnectionType.UNKNOWN, false);
    private final Histogram clientHitHisto = new Histogram(10, 1000L);
    private UAgentInfo userAgentInfo;
    private BrowserType bt;
    private BrowserOS os;
    private boolean isMobile;
    private boolean isBanned;

    private TLongArrayStack latencies = new TLongArrayStack(100);

    public WebClientInfo(String clientAddr) {
        this.clientAddr = clientAddr;
    }

    public void setConnectionType(WebConnectionType connectionType) {
        webConnection.setConnectionType(connectionType);
    }

    public void setWebConnection(WebConnection webConnection) {
        this.webConnection = webConnection;
    }

    public WebConnection getWebConnection() {
        return webConnection;
    }

    public WebConnectionType getConnectionType() {
        return webConnection.getConnectionType();
    }

    public String getClientAddr() {
        return clientAddr;
    }

    public String getUserAgent() {
        return webConnection.getUserAgent();
    }

    public WebConnection getConnection() {
        return webConnection;
    }

    public String getHost() {
        return webConnection.getHost();
    }

    public int getPort() {
        return webConnection.getPort();
    }

    public BrowserType getBrowserType() {
        return bt;
    }

    public void setBrowserType(BrowserType bt) {
        this.bt = bt;
    }

    public BrowserOS getOs() {
        return os;
    }

    public void setOs(BrowserOS os) {
        this.os = os;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setMobile(boolean mobile) {
        isMobile = mobile;
    }

    public UAgentInfo getUserAgentInfo() {
        return userAgentInfo;
    }

    public void setUserAgentInfo(UAgentInfo agentInfo) {
        this.userAgentInfo = agentInfo;
    }

    public Histogram getClientHitHisto() {
        return clientHitHisto;
    }

    public void logHit(long now) {
        clientHitHisto.hit(now);
    }

    public int getTotalHitCount(long now) {
        return clientHitHisto.getTotalHitCount(now);
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getInstrument() {
        return webConnection.getInstrument();
    }

    public void setInstrument(String instrument) {
        this.webConnection.setInstrument(instrument);
    }

    public boolean isScoreClient() {
        return webConnection.isScoreClient();
    }

    public void setScoreClient(boolean isScoreClient) {
        this.webConnection.setScoreClient(isScoreClient);
    }

    public long getLatency() {
        long[] ls = latencies.toArray();
        long sum = 0L;
        int counter = 0;
        for (long l : ls) {
            if (sum + l >= Long.MAX_VALUE) {
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
        if (latencies.size() >= 100) {
            latencies.pop();
        }
        this.latencies.push(latency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebClientInfo that = (WebClientInfo) o;
        return clientAddr.equals(that.clientAddr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientAddr);
    }

    @Override
    public String toString() {
        return "WebClientInfo{" +
                "clientAddr='" + clientAddr + '\'' +
                ", bt=" + bt +
                ", os=" + os +
                ", isMobile=" + isMobile +
                ", isBanned=" + isBanned +
                ", webConnection=" + webConnection +
                '}';
    }
}
