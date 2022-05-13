package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.web.*;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;

public interface ScoreService {

    Score loadScore(File file);

    boolean reset();

    void play(long startMillis);

    void stopPlay();

    void setPosition(long millis);

    void subscribe(SzcoreEngineEventListener eventListener);

    void subscribe(WebAudienceStateListener eventListener);

    void setTempoModifier(Id transportId, TempoModifier tempoModifier);

    void setRandomisationStrategy(List<Integer> randomisationStrategy);

    void usePageRandomisation(Boolean value);

    void useContinuousPageChange(Boolean value);

    void setOverlayValue(OverlayType type, long value, List<Id> instrumentIds);

    void setOverlayText(OverlayType type, String txt, boolean isVisible, List<Id> instrumentIds);

    void onUseOverlayLine(OverlayType type, Boolean value, List<Id> instrumentIds);

    void onUseOverlay(OverlayType type, Boolean value, List<Id> instrumentIds);

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

    List<WebClientInfo> getWebScoreInstrumentClients(String instrument);

    void setWebDelayMs(long delayMs);
}
