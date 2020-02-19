package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.IncomingWebEvent;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.web.WebScoreEventListener;
import com.xenaksys.szcore.web.ZsHttpRequest;
import com.xenaksys.szcore.web.ZsHttpResponse;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

public interface ScoreService {

    void loadScoreAndPrepare(String filePath);

    Score loadScore(File file);

    boolean reset();

    void play(long startMillis);

    void stopPlay();

    void setPosition(long millis);

    void subscribe(SzcoreEngineEventListener eventListener);

    void subscribe(WebScoreEventListener eventListener);

    void setTempoModifier(Id transportId, TempoModifier tempoModifier);

    void setRandomisationStrategy(List<Integer> randomisationStrategy);

    void usePageRandomisation(Boolean value);

    void useContinuousPageChange(Boolean value);

    void setDynamicsValue(long value, List<Id> instrumentIds);

    void onUseDynamicsOverlay(Boolean value, List<Id> instrumentIds);

    void onUseDynamicsLine(Boolean value, List<Id> instrumentIds);

    void setPressureValue(long value, List<Id> instrumentIds);

    void onUsePressureOverlay(Boolean value, List<Id> instrumentIds);

    void onUsePressureLine(Boolean value, List<Id> instrumentIds);

    void setSpeedValue(long value, List<Id> instrumentIds);

    void onUseSpeedOverlay(Boolean value, List<Id> instrumentIds);

    void onUseSpeedLine(Boolean value, List<Id> instrumentIds);

    void setPositionValue(long value, List<Id> instrumentIds);

    void onUsePositionOverlay(Boolean value, List<Id> instrumentIds);

    void onUsePositionLine(Boolean value, List<Id> instrumentIds);

    void setContentValue(long value, List<Id> instrumentIds);

    void onUseContentOverlay(Boolean value, List<Id> instrumentIds);

    void onUseContentLine(Boolean value, List<Id> instrumentIds);

    void addBroadcastPort(InetAddress addr, int port);

    InetAddress getBroadcastAddress();

    void setBroadcastAddress(InetAddress broadcastAddress);

    List<InetAddress> getDetectedBroadcastAddresses();

    InetAddress getServerAddress();

    int getInscorePort();

    void setInscorePort(int inscorePort);

    String getSubnetMask();

    void setSubnetMask(String subnetMask);

    void initNetInfo();

    List<NetUtil.NetworkDevice> getParallelConnectedNetworkClients();

    ZsHttpResponse onHttpRequest(ZsHttpRequest zsRequest);

    void startWebServer();

    void stopWebServer();

    boolean isWebServerRunning();

    void onIncomingWebEvent(IncomingWebEvent webEvent);

    void pushToWebClients(String data);
}
