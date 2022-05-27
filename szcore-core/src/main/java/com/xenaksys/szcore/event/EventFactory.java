package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.gui.ParticipantEvent;
import com.xenaksys.szcore.event.gui.ParticipantStatsEvent;
import com.xenaksys.szcore.event.gui.PrecountInfo;
import com.xenaksys.szcore.event.gui.ScoreInfoEvent;
import com.xenaksys.szcore.event.gui.ScoreSectionInfoEvent;
import com.xenaksys.szcore.event.gui.StrategyEvent;
import com.xenaksys.szcore.event.gui.StrategyEventType;
import com.xenaksys.szcore.event.gui.WebAudienceClientInfoUpdateEvent;
import com.xenaksys.szcore.event.gui.WebScoreClientInfoUpdateEvent;
import com.xenaksys.szcore.event.music.ModWindowEvent;
import com.xenaksys.szcore.event.music.PrecountBeatSetupEvent;
import com.xenaksys.szcore.event.music.PrepStaveChangeEvent;
import com.xenaksys.szcore.event.music.ScoreSectionEvent;
import com.xenaksys.szcore.event.music.ScoreSectionEventType;
import com.xenaksys.szcore.event.music.StopEvent;
import com.xenaksys.szcore.event.music.TimeSigChangeEvent;
import com.xenaksys.szcore.event.music.TransitionEvent;
import com.xenaksys.szcore.event.music.TransportPositionEvent;
import com.xenaksys.szcore.event.osc.AddPartsEvent;
import com.xenaksys.szcore.event.osc.BeatScriptEvent;
import com.xenaksys.szcore.event.osc.DateTickEvent;
import com.xenaksys.szcore.event.osc.ElementAlphaEvent;
import com.xenaksys.szcore.event.osc.ElementColorEvent;
import com.xenaksys.szcore.event.osc.ElementSelectedAudienceEvent;
import com.xenaksys.szcore.event.osc.ElementYPositionEvent;
import com.xenaksys.szcore.event.osc.HelloEvent;
import com.xenaksys.szcore.event.osc.InstrumentResetSlotsEvent;
import com.xenaksys.szcore.event.osc.InstrumentSlotsEvent;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.osc.OscScriptEvent;
import com.xenaksys.szcore.event.osc.OscStaveActivateEvent;
import com.xenaksys.szcore.event.osc.OscStaveTempoEvent;
import com.xenaksys.szcore.event.osc.OscStopEvent;
import com.xenaksys.szcore.event.osc.OverlayTextEvent;
import com.xenaksys.szcore.event.osc.PageDisplayEvent;
import com.xenaksys.szcore.event.osc.PageMapDisplayEvent;
import com.xenaksys.szcore.event.osc.PartEvent;
import com.xenaksys.szcore.event.osc.PingEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOffEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOnEvent;
import com.xenaksys.szcore.event.osc.ResetInstrumentEvent;
import com.xenaksys.szcore.event.osc.ResetScoreEvent;
import com.xenaksys.szcore.event.osc.ResetStavesEvent;
import com.xenaksys.szcore.event.osc.SendServerIpBroadcastEvent;
import com.xenaksys.szcore.event.osc.ServerHelloEvent;
import com.xenaksys.szcore.event.osc.StaveActiveChangeEvent;
import com.xenaksys.szcore.event.osc.StaveClockTickEvent;
import com.xenaksys.szcore.event.osc.StaveDateTickEvent;
import com.xenaksys.szcore.event.osc.StaveDyTickEvent;
import com.xenaksys.szcore.event.osc.StaveStartMarkEvent;
import com.xenaksys.szcore.event.osc.StaveYPositionEvent;
import com.xenaksys.szcore.event.osc.TempoChangeEvent;
import com.xenaksys.szcore.event.osc.TitleEvent;
import com.xenaksys.szcore.event.osc.TransitionScriptEvent;
import com.xenaksys.szcore.event.osc.VoteAudienceEvent;
import com.xenaksys.szcore.event.script.ScriptingEngineEvent;
import com.xenaksys.szcore.event.script.ScriptingEngineResetEvent;
import com.xenaksys.szcore.event.web.audience.UpdateWebAudienceConnectionsEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceAudioEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceAudioEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceInstructionsEvent;
import com.xenaksys.szcore.event.web.audience.WebAudiencePlayTilesEvent;
import com.xenaksys.szcore.event.web.audience.WebAudiencePrecountEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceRequestLogEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceResetEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceSelectTilesEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStateUpdateEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStopEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceVoteEvent;
import com.xenaksys.szcore.event.web.audience.WebPollAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebStartAudienceEvent;
import com.xenaksys.szcore.event.web.in.UpdateWebScoreConnectionsEvent;
import com.xenaksys.szcore.event.web.in.WebScoreClientHelloEvent;
import com.xenaksys.szcore.event.web.in.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartReadyEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartRegEvent;
import com.xenaksys.szcore.event.web.in.WebScoreRemoveConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectInstrumentSlotEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectSectionEvent;
import com.xenaksys.szcore.event.web.out.DelayedOutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEventType;
import com.xenaksys.szcore.model.HistoBucketView;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.Transition;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.InscoreMapElement;
import com.xenaksys.szcore.score.OverlayElementType;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.score.web.audience.AudioComponentType;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;
import com.xenaksys.szcore.scripting.ScriptingEngineScript;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebConnection;
import com.xenaksys.szcore.web.WebScoreStateType;
import com.xenaksys.szcore.web.ZsWebRequest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventFactory {

    List<Object> oscClockArgs = new ArrayList<>();
    List<Object> oscDateArgs = new ArrayList<>();
    List<Object> oscDyArgs = new ArrayList<>();
    List<Object> oscYPositionArgs = new ArrayList<>();
    List<Object> oscAlphaArgs = new ArrayList<>();
    List<Object> oscPenAlphaArgs = new ArrayList<>();
    List<Object> oscColorArgs = new ArrayList<>();

    public EventFactory() {
        init();
    }

    private void init() {
        oscClockArgs.add(Consts.OSC_ARG_CLOCK);
        oscDateArgs.add(Consts.OSC_ARG_DATE);
        oscDyArgs.add(Consts.OSC_ARG_DY);
        oscYPositionArgs.add(Consts.OSC_ARG_Y_POSITION);
        oscAlphaArgs.add(Consts.OSC_ARG_ALPHA);
        oscPenAlphaArgs.add(Consts.OSC_ARG_PEN_ALPHA);
        oscColorArgs.add(Consts.OSC_ARG_PEN_COLOR);
    }

    public TempoChangeEvent createTempoChangeEvent(Tempo tempo,
                                                   BeatId changeOnBaseBeat,
                                                   Id transportId,
                                                   List<OscStaveTempoEvent> oscEvents,
                                                   long creationTime) {
        return new TempoChangeEvent(tempo, changeOnBaseBeat, transportId, oscEvents, creationTime);
    }


    public TransportPositionEvent createTransportPositionEvent(Id transportId,
                                                               int startBaseBeatNo,
                                                               int transportBeatNo,
                                                               int tickNo,
                                                               long positionMillis,
                                                               long creationTime) {
        return new TransportPositionEvent(transportId, startBaseBeatNo, transportBeatNo, tickNo, positionMillis, creationTime);
    }

    public StopEvent createStopEvent(BeatId lastEvent, Id transportId, long creationTime) {
        return new StopEvent(lastEvent, transportId, creationTime);
    }

    public ScoreSectionEvent createScoreSectionEvent(BeatId lastEvent, String section, ScoreSectionEventType sectionEventType, Id transportId, long creationTime) {
        return new ScoreSectionEvent(lastEvent, section, sectionEventType, transportId, creationTime);
    }

    public ModWindowEvent createModWindowEvent(BeatId beatId, Page nextPage, PageId currentPageId, Stave stave, boolean isOpen, long creationTime) {
        return new ModWindowEvent(beatId, nextPage, currentPageId, stave, isOpen, creationTime);
    }

    public TimeSigChangeEvent createTimeSigChangeEvent(TimeSignature timeSignature,
                                                       BeatId changeOnBaseBeat,
                                                       Id transportId,
                                                       long creationTime) {
        return new TimeSigChangeEvent(timeSignature, changeOnBaseBeat, transportId, creationTime);
    }

    public StaveActiveChangeEvent createActiveStaveChangeEvent(StaveId staveId,
                                                               boolean isActive,
                                                               boolean isPlayStave,
                                                               BeatId changeOnBaseBeat,
                                                               String destination,
                                                               long creationTime) {
        return new StaveActiveChangeEvent(staveId, isActive, isPlayStave, changeOnBaseBeat, createStaveActivateEvent(staveId, destination, isActive, isPlayStave, creationTime), creationTime);
    }

    public PrepStaveChangeEvent createPrepStaveChangeEvent(BeatId executeOnBaseBeat,
                                                           BeatId activateOnBaseBeat,
                                                           BeatId deactivateOnBaseBeat,
                                                           BeatId pageChangeOnBaseBeat,
                                                           PageId nextPageId,
                                                           long creationTime) {
        return new PrepStaveChangeEvent(executeOnBaseBeat, activateOnBaseBeat, deactivateOnBaseBeat, pageChangeOnBaseBeat, nextPageId, creationTime);
    }

    public ParticipantEvent createParticipantEvent(String clientId, InetAddress inetAddress, String hostAddress, int portIn, int portOut, int portErr, int ping, String instrument, boolean isReady, boolean isBanned, long creationTime) {
        return new ParticipantEvent(clientId, inetAddress, hostAddress, portIn, portOut, portErr, ping, instrument, isReady, isBanned, creationTime);
    }

    public StrategyEvent createStrategyEvent(StrategyEventType strategyEventType, long creationTime) {
        return new StrategyEvent(strategyEventType, creationTime);
    }

    public WebAudienceClientInfoUpdateEvent createWebAudienceClientInfoUpdateEvent(ArrayList<WebClientInfo> webClientInfos, List<HistoBucketView> histoBucketViews, int totalWebHits, long creationTime) {
        return new WebAudienceClientInfoUpdateEvent(webClientInfos, histoBucketViews, totalWebHits, creationTime);
    }

    public WebScoreClientInfoUpdateEvent createWebScoreClientInfoUpdateEvent(ArrayList<WebClientInfo> webClientInfos, boolean isFullUpdate, long creationTime) {
        return new WebScoreClientInfoUpdateEvent(webClientInfos, isFullUpdate, creationTime);
    }

    public ParticipantStatsEvent createParticipantStatsEvent(InetAddress inetAddress, String hostAddress, int port, double pingLatencyMillis, double halfPingLatencyMillis, boolean isExpired, long lastPingLatency, long creationTime) {
        return new ParticipantStatsEvent(inetAddress, hostAddress, port, pingLatencyMillis, halfPingLatencyMillis, isExpired, lastPingLatency, creationTime);
    }

    public ScoreSectionInfoEvent createScoreSectionInfoEvent(Id scoreId, List<SectionInfo> sectionInfos, List<String> sectionOrder, boolean isReady, String currentSection, String nextSection, long creationTime) {
        return new ScoreSectionInfoEvent(scoreId, sectionInfos, sectionOrder, isReady, currentSection, nextSection, creationTime);
    }

    public ScoreInfoEvent createScoreInfoEvent(Id scoreId, boolean isStop, PrecountInfo precountInfo, long creationTime) {
        return new ScoreInfoEvent(scoreId, isStop, precountInfo, creationTime);
    }

    public OscEvent createOscEvent(String address, List<Object> args, String destination, long creationTime) {
        return new OscEvent(address, args, null, destination, creationTime);
    }

    public StaveClockTickEvent createStaveClockTickEvent(String address, String destination, StaveId staveId, long creationTime) {
        return new StaveClockTickEvent(address, oscClockArgs, destination, staveId, creationTime);
    }

    public StaveDateTickEvent createStaveDateTickEvent(String address, String destination, StaveId staveId, long creationTime) {
        return new StaveDateTickEvent(address, new ArrayList<>(oscDateArgs), destination, staveId, creationTime);
    }

    public StaveDateTickEvent createStaveDateTickEvent(String address, String destination, StaveId staveId, int beatNo, long creationTime) {
        return new StaveDateTickEvent(address, new ArrayList<>(oscDateArgs), destination, staveId, beatNo, creationTime);
    }

    public StaveStartMarkEvent createStaveStartMarkEvent(String address, String destination, StaveId staveId, int beatNo, long creationTime) {
        return new StaveStartMarkEvent(address, new ArrayList<>(oscDateArgs), destination, staveId, beatNo, creationTime);
    }

    public DateTickEvent createDateTickEvent(String destination, StaveId staveId, int beatNo, long creationTime) {
        return new DateTickEvent(createJavaScriptArgs(), destination, staveId, beatNo, creationTime);
    }

    public StaveDyTickEvent createStaveDyTickEvent(String address, String destination, StaveId staveId, long creationTime) {
        return new StaveDyTickEvent(address, oscDyArgs, destination, staveId, creationTime);
    }

    public StaveYPositionEvent createStaveYPositionEvent(String address, String destination, StaveId staveId, long creationTime) {
        return new StaveYPositionEvent(address, oscYPositionArgs, destination, staveId, creationTime);
    }

    public ElementYPositionEvent createElementYPositionEvent(String address, String destination, StaveId staveId, long unscaledValue, OverlayType overlayType, long creationTime) {
        return new ElementYPositionEvent(address, oscYPositionArgs, unscaledValue, overlayType, destination, staveId, creationTime);
    }

    public ElementAlphaEvent createElementAlphaEvent(StaveId staveId, boolean isEnabled, OverlayType overlayType, OverlayElementType overlayElementType, String address, String destination, long creationTime) {
        return new ElementAlphaEvent(staveId, isEnabled, overlayType, overlayElementType, address, oscAlphaArgs, destination, creationTime);
    }

    public OverlayTextEvent createOverlayTextEvent(StaveId staveId, String l1, String l2, String l3, boolean isVisible, OverlayType overlayType, String destination, long creationTime) {
        return new OverlayTextEvent(staveId, l1, l2, l3, isVisible, overlayType, destination, creationTime);
    }

    public ElementAlphaEvent createElementPenAlphaEvent(StaveId staveId, boolean isEnabled, OverlayType overlayType, OverlayElementType overlayElementType, String address, String destination, long creationTime) {
        return new ElementAlphaEvent(staveId, isEnabled, overlayType, overlayElementType, address, oscPenAlphaArgs, destination, creationTime);
    }

    public ElementColorEvent createElementColorEvent(StaveId staveId, OverlayType overlayType, String address, String destination, long creationTime) {
        return new ElementColorEvent(staveId, overlayType, address, oscColorArgs, destination, creationTime);
    }

    public PageDisplayEvent createPageDisplayEvent(PageId pageId, PageId rndPageId, String filename, StaveId staveId, String address, List<Object> args, BeatId eventBaseBeat, String destination, long creationTime) {
        return new PageDisplayEvent(pageId, rndPageId, filename, staveId, address, args, eventBaseBeat, destination, creationTime);
    }

    public PageMapDisplayEvent createPageMapDisplayEvent(PageId pageId, StaveId staveId, String address, List<Object> args, List<InscoreMapElement> mapElements, BeatId eventBaseBeat, String destination, long creationTime) {
        return new PageMapDisplayEvent(pageId, staveId, address, args, mapElements, eventBaseBeat, destination, creationTime);
    }

    public OscStaveActivateEvent createStaveActivateEvent(StaveId staveId, String destination, boolean isActive, boolean isPlayStave, long creationTime) {
        return new OscStaveActivateEvent(staveId, createJavaScriptArgs(), destination, isActive, isPlayStave, creationTime);
    }

    public OscStaveTempoEvent createOscStaveTempoEvent(String destination, int tempo, long creationTime) {
        return new OscStaveTempoEvent(createJavaScriptArgs(), destination, tempo, creationTime);
    }

    public OscStopEvent createOscStopEvent(String destination, long creationTime) {
        return new OscStopEvent(createJavaScriptArgs(), destination, creationTime);
    }

    public PrecountBeatSetupEvent createPrecountBeatSetupEvent(int precountBeatNo, long precountTimeMillis, long initBeaterInterval, Id transportId, long creationTime) {
        return new PrecountBeatSetupEvent(true, precountBeatNo, precountTimeMillis, initBeaterInterval, transportId, creationTime);
    }

    public PrecountBeatOnEvent createPrecountBeatOnEvent(String destination, long creationTime) {
        return new PrecountBeatOnEvent(createJavaScriptArgs(), destination, creationTime);
    }

    public PrecountBeatOffEvent createPrecountBeatOffEvent(String destination, long creationTime) {
        return new PrecountBeatOffEvent(createJavaScriptArgs(), destination, creationTime);
    }

    public TitleEvent createTitleEvent(String destination, String title, long creationTime) {
        TitleEvent titleEvent = new TitleEvent(createJavaScriptArgs(), destination, creationTime);
        titleEvent.addCommandArg(title);
        return titleEvent;
    }

    public PartEvent createPartEvent(String destination, String part, long creationTime) {
        PartEvent partEvent = new PartEvent(createJavaScriptArgs(), destination, creationTime);
        partEvent.addCommandArg(part);
        return partEvent;
    }

    public BeatScriptEvent createBeatScriptEvent(String destination, BeatId beatId, long creationTime) {
        return new BeatScriptEvent(createJavaScriptArgs(), beatId, destination, creationTime);
    }

    public OscScriptEvent createOscScriptEvent(String destination, BeatId beatId, String address, List<Object> args, long creationTime) {
        return new OscScriptEvent(address, args, beatId, destination, creationTime);
    }

    public TransitionEvent createTransitionEvent(String destination, BeatId beatId, Transition transition, long creationTime) {
        return new TransitionEvent(beatId, destination, transition, creationTime);
    }

    public TransitionScriptEvent createTransitionScriptEvent(String destination, long creationTime) {
        return new TransitionScriptEvent(createJavaScriptArgs(), destination, creationTime);
    }

    public HelloEvent createHelloEvent(String destination, long creationTime) {
        return new HelloEvent(Consts.OSC_INSCORE_ADDRESS_ROOT, createHelloArgs(), destination, creationTime);
    }

    public ServerHelloEvent createServerHelloEvent(String localAddr, String destination, long creationTime) {
        ServerHelloEvent ping = new ServerHelloEvent(createJavaScriptArgs(), destination, creationTime);
        ping.addCommandArg(localAddr);
        return ping;
    }

    public PingEvent createPingEvent(String destination, long creationTime) {
        PingEvent ping = new PingEvent(createJavaScriptArgs(), destination, creationTime);
        ping.addCommandArg(creationTime);
        return ping;
    }

    public AddPartsEvent createAddPartsEvent(String instrumentsCsv, String destination, long creationTime) {
        AddPartsEvent event = new AddPartsEvent(createJavaScriptArgs(), destination, creationTime);
        event.addCommandArg(instrumentsCsv);
        return event;
    }

    public InstrumentSlotsEvent createInstrumentSlotsEvent(String instrumentsCsv, String destination, long creationTime, BeatId beatId) {
        InstrumentSlotsEvent event = new InstrumentSlotsEvent(createJavaScriptArgs(), instrumentsCsv, beatId, destination, creationTime);
        event.addCommandArg(instrumentsCsv);
        return event;
    }

    public InstrumentResetSlotsEvent createResetInstrumentSlotsEvent(String destination, long creationTime, BeatId beatId) {
        InstrumentResetSlotsEvent event = new InstrumentResetSlotsEvent(createJavaScriptArgs(), beatId, destination, creationTime);
        event.addCommandArg();
        return event;
    }

    public SendServerIpBroadcastEvent createServerIpBroadcastEvent(String serverIp, String destination, long creationTime) {
        SendServerIpBroadcastEvent event = new SendServerIpBroadcastEvent(createJavaScriptArgs(), destination, creationTime);
        event.addCommandArg(serverIp);
        return event;
    }

    public ResetScoreEvent createResetScoreEvent(String destination, long creationTime) {
        ResetScoreEvent resetScoreEvent = new ResetScoreEvent(createJavaScriptArgs(), destination, creationTime);
        resetScoreEvent.addCommandArg();
        return resetScoreEvent;
    }

    public ResetInstrumentEvent createResetInstrumentEvent(String destination, long creationTime) {
        ResetInstrumentEvent resetScoreEvent = new ResetInstrumentEvent(createJavaScriptArgs(), destination, creationTime);
        resetScoreEvent.addCommandArg();
        return resetScoreEvent;
    }

    public ResetStavesEvent createResetStavesEvent(String destination, long creationTime) {
        ResetStavesEvent resetScoreEvent = new ResetStavesEvent(createJavaScriptArgs(), destination, creationTime);
        resetScoreEvent.addCommandArg();
        return resetScoreEvent;
    }

    public ElementSelectedAudienceEvent createElementSelectedEvent(String elementId, boolean isSelected, String eventId, String sourceAddr, String requestPath,
                                                                   long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        return new ElementSelectedAudienceEvent(elementId, isSelected, sourceAddr, requestPath, eventId, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public VoteAudienceEvent createVoteEvent(String value, int usersNo, String eventId, String sourceAddr, String requestPath,
                                             long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        return new VoteAudienceEvent(value, usersNo, sourceAddr, requestPath, eventId, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public UpdateWebAudienceConnectionsEvent createUpdateWebAudienceConnectionsEvent(Set<WebConnection> clientConnections, long creationTime) {
        return new UpdateWebAudienceConnectionsEvent(clientConnections, creationTime);
    }

    public WebAudienceRequestLogEvent createWebAudienceRequestLogEvent(ZsWebRequest zsRequest, long creationTime) {
        return new WebAudienceRequestLogEvent(zsRequest, creationTime);
    }

    public WebPollAudienceEvent createWebAudiencePollEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        return new WebPollAudienceEvent(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public WebStartAudienceEvent createWebAudienceStartEvent(String eventId, String sourceAddr, String requestPath,
                                                             long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        return new WebStartAudienceEvent(sourceAddr, requestPath, eventId, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public WebAudienceEvent createWebAudienceEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, long creationTime) {
        return new WebAudienceEvent(beatId, scripts, creationTime);
    }

    public ScriptingEngineEvent createScriptingEngineEvent(BeatId beatId, List<ScriptingEngineScript> scripts, long creationTime) {
        return new ScriptingEngineEvent(beatId, scripts, creationTime);
    }

    public ScriptingEngineResetEvent createScriptingEngineResetEvent(BeatId beatId, List<ScriptingEngineScript> scripts, long creationTime) {
        return new ScriptingEngineResetEvent(beatId, scripts, creationTime);
    }

    public WebAudienceInstructionsEvent createWebAudienceInstructionsEvent(String l1, String l2, String l3, boolean isVisible, long creationTime) {
        return new WebAudienceInstructionsEvent(l1, l2, l3, isVisible, creationTime);
    }

    public WebAudienceAudioEvent createWebAudienceAudioEvent(AudioComponentType componentType, WebAudienceAudioEventType eventType, double value, int durationMs, long creationTime) {
        return new WebAudienceAudioEvent(componentType, eventType, value, durationMs, creationTime);
    }

    public WebAudienceStateUpdateEvent createWebAudienceStateUpdateEvent(WebScoreStateType propertyName, Object propertyValue, long creationTime) {
        return new WebAudienceStateUpdateEvent(propertyName, propertyValue, creationTime);
    }

    public WebAudiencePrecountEvent createWebAudiencePrecountEvent(int count, boolean isOn, int colourId, long creationTime) {
        return new WebAudiencePrecountEvent(count, isOn, colourId, creationTime);
    }

    public WebAudienceResetEvent createWebAudienceResetEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, long creationTime) {
        return new WebAudienceResetEvent(beatId, scripts, creationTime);
    }

    public WebAudienceStopEvent createWebAudienceStopEvent(long creationTime) {
        return new WebAudienceStopEvent(null, null, creationTime);
    }

    public WebAudiencePlayTilesEvent createWebAudiencePlayTilesEvent(long creationTime) {
        return new WebAudiencePlayTilesEvent(null, null, creationTime);
    }

    public WebAudienceSelectTilesEvent createWebAudienceSelectTilesEvent(List<String> tileIds, long creationTime) {
        return new WebAudienceSelectTilesEvent(null, null, tileIds, creationTime);
    }

    public WebAudienceVoteEvent createWebAudienceVoteEvent(String value, int usersNo, long creationTime) {
        return new WebAudienceVoteEvent(null, null, value, usersNo, creationTime);
    }

    public OutgoingWebEvent createOutgoingWebAudienceEvent(BeatId beatId, String eventId, OutgoingWebEventType eventType, boolean isSendNow, long creationTime) {
        return new OutgoingWebEvent(eventId, beatId, EventType.WEB_AUDIENCE_OUT, eventType, isSendNow, creationTime);
    }

    public OutgoingWebEvent createWebScoreOutEvent(BeatId beatId, String eventId, OutgoingWebEventType eventType, boolean isSendNow, long creationTime) {
        return new OutgoingWebEvent(eventId, beatId, EventType.WEB_SCORE_OUT, eventType, isSendNow, creationTime);
    }

    public DelayedOutgoingWebEvent createDelayedWebScoreOutEvent(OutgoingWebEvent outgoingWebEvent, long creationTime) {
        return new DelayedOutgoingWebEvent(outgoingWebEvent, creationTime);
    }

    public UpdateWebScoreConnectionsEvent createUpdateWebScoreConnectionsEvent(Set<WebConnection> clientConnections, long creationTime) {
        return new UpdateWebScoreConnectionsEvent(clientConnections, creationTime);
    }

    public WebScoreRemoveConnectionEvent createRemoveWebScoreConnectionsEvent(List<String> connectionIds, long creationTime) {
        return new WebScoreRemoveConnectionEvent(connectionIds, creationTime);
    }

    public WebScoreConnectionEvent createWebScoreConnectionEvent(WebClientInfo webClientInfo, long creationTime) {
        return new WebScoreConnectionEvent(webClientInfo, creationTime);
    }

    public WebScoreClientHelloEvent createWebScoreClientHelloEvent(String clientId, String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        return new WebScoreClientHelloEvent(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
    }

    public WebScorePartRegEvent createWebScorePartRegEvent(String clientId, String eventId, String sourceAddr, String part, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        return new WebScorePartRegEvent(clientId, eventId, sourceAddr, part, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
    }

    public WebScorePartReadyEvent createWebScorePartReadyEvent(String clientId, String eventId, String sourceAddr, String part, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        return new WebScorePartReadyEvent(clientId, eventId, sourceAddr, part, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
    }

    public WebScoreSelectInstrumentSlotEvent createWebScoreSelectInstrumentSlotEvent(String clientId, String eventId, String sourceAddr, String part, int slotNo, String slotInstrument, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        return new WebScoreSelectInstrumentSlotEvent(clientId, eventId, sourceAddr, part, slotNo, slotInstrument, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
    }

    public WebScoreSelectSectionEvent createWebScoreSelectSectionEvent(String clientId, String eventId, String sourceAddr, String section, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        return new WebScoreSelectSectionEvent(clientId, eventId, sourceAddr, section, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
    }

    public List<Object> createJavaScriptArgs() {
        List<Object> jsArgs = new ArrayList<>();
        jsArgs.add(Consts.RUN);
        return jsArgs;
    }

    public List<Object> createHelloArgs() {
        List<Object> jsArgs = new ArrayList<>();
        jsArgs.add(Consts.HELLO);
        return jsArgs;
    }
}
