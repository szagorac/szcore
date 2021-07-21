package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.score.web.audience.WebAudienceScore;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebConnection;
import com.xenaksys.szcore.web.WebScoreStateListener;
import com.xenaksys.szcore.web.ZsWebRequest;
import com.xenaksys.szcore.web.ZsWebResponse;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;

public interface ScoreService {

    void loadScoreAndPrepare(String filePath);

    Score loadScore(File file);

    WebAudienceScore loadWebScore(File file);

    boolean reset();

    void play(long startMillis);

    void stopPlay();

    void setPosition(long millis);

    void subscribe(SzcoreEngineEventListener eventListener);

    void subscribe(WebScoreStateListener eventListener);

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

    int getMaxPort();

    void setMaxPort(int maxPort);

    String getSubnetMask();

    void setSubnetMask(String subnetMask);

    void initNetInfo();

    ZsWebResponse onWebRequest(ZsWebRequest zsRequest);

    void onWebConnection(WebConnection webConnection);

    void startAudienceWebServer();

    void stopAudienceWebServer();

    boolean isAudienceWebServerRunning();

    void onIncomingWebAudienceEvent(IncomingWebAudienceEvent webEvent);

    void pushToWebAudience(String data);

    void updateAudienceWebServerConnections(Set<WebConnection> connections);

    void updateScoreServerConnections(Set<WebConnection> connections);

    void banWebClient(WebClientInfo clientInfo);

    void banConnection(String clientId);

    void onIncomingWebScoreEvent(WebScoreInEvent webEvent);

    void pushToScoreWeb(String target, WebScoreTargetType targetType, String data);

    void closeScoreConnections(List<String> connectionIds);

    void onWebScorePing(WebClientInfo clientInfo, long serverTime, long eventTime);

    void onInterceptedOscOutEvent(OscEvent event);

}
