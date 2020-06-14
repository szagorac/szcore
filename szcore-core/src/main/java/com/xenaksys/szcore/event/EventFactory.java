package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.Transition;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.WebScoreScript;
import com.xenaksys.szcore.web.WebConnection;

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

    public TimeSigChangeEvent createTimeSigChangeEvent(TimeSignature timeSignature,
                                                       BeatId changeOnBaseBeat,
                                                       Id transportId,
                                                       long creationTime) {
        return new TimeSigChangeEvent(timeSignature, changeOnBaseBeat, transportId, creationTime);
    }

    public StaveActiveChangeEvent createActiveStaveChangeEvent(StaveId staveId,
                                                               boolean isActive,
                                                               BeatId changeOnBaseBeat,
                                                               String destination,
                                                               long creationTime) {
        return new StaveActiveChangeEvent(staveId, isActive, changeOnBaseBeat, createStaveActivateEvent(destination, creationTime), creationTime);
    }

    public PrepStaveChangeEvent createPrepStaveChangeEvent(BeatId executeOnBaseBeat,
                                                           BeatId activateOnBaseBeat,
                                                           BeatId deactivateOnBaseBeat,
                                                           BeatId pageChangeOnBaseBeat,
                                                           PageId nextPageId,
                                                           long creationTime) {
        return new PrepStaveChangeEvent(executeOnBaseBeat, activateOnBaseBeat, deactivateOnBaseBeat, pageChangeOnBaseBeat, nextPageId, creationTime);
    }

    public ParticipantEvent createParticipantEvent(InetAddress inetAddress, String hostAddress, int portIn, int portOut, int portErr, int ping, String instrument, long creationTime) {
        return new ParticipantEvent(inetAddress, hostAddress, portIn, portOut, portErr, ping, instrument, creationTime);
    }

    public ParticipantStatsEvent createParticipantStatsEvent(InetAddress inetAddress, String hostAddress, int port, double pingLatencyMillis, double halfPingLatencyMillis, boolean isExpired, long lastPingLatency, long creationTime) {
        return new ParticipantStatsEvent(inetAddress, hostAddress, port, pingLatencyMillis, halfPingLatencyMillis, isExpired, lastPingLatency, creationTime);
    }

    public OscEvent createOscEvent(String address, List<Object> arguments, BeatId eventBaseBeat, long creationTime) {
        return new OscEvent(address, arguments, eventBaseBeat, creationTime);
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

    public DateTickEvent createDateTickEvent(String destination, int staveId, int beatNo, long creationTime) {
        return new DateTickEvent(createJavaScriptArgs(), destination, staveId, beatNo, creationTime);
    }

    public StaveDyTickEvent createStaveDyTickEvent(String address, String destination, StaveId staveId, long creationTime) {
        return new StaveDyTickEvent(address, oscDyArgs, destination, staveId, creationTime);
    }

    public StaveYPositionEvent createStaveYPositionEvent(String address, String destination, StaveId staveId, long creationTime) {
        return new StaveYPositionEvent(address, oscYPositionArgs, destination, staveId, creationTime);
    }

    public ElementYPositionEvent createElementYPositionEvent(String address, String destination, StaveId staveId, long creationTime) {
        return new ElementYPositionEvent(address, oscYPositionArgs, destination, staveId, creationTime);
    }

    public ElementAlphaEvent createElementAlphaEvent(String address, String destination, long creationTime) {
        return new ElementAlphaEvent(address, oscAlphaArgs, destination, creationTime);
    }

    public ElementAlphaEvent createElementPenAlphaEvent(String address, String destination, long creationTime) {
        return new ElementAlphaEvent(address, oscPenAlphaArgs, destination, creationTime);
    }

    public ElementColorEvent createElementColorEvent(String address, String destination, long creationTime) {
        return new ElementColorEvent(address, oscColorArgs, destination, creationTime);
    }

    public OscEvent createPageDisplayEvent(String address, List<Object> args, BeatId eventBaseBeat, String destination, long creationTime) {
        return new OscEvent(address, args, eventBaseBeat, destination, creationTime);
    }

    public OscEvent createPageMapDisplayEvent(String address, List<Object> args, BeatId eventBaseBeat, String destination, long creationTime) {
        return new OscEvent(address, args, eventBaseBeat, destination, creationTime);
    }

    public OscStaveActivateEvent createStaveActivateEvent(String destination, long creationTime) {
        return new OscStaveActivateEvent(createJavaScriptArgs(), destination, creationTime);
    }

    public OscStaveTempoEvent createOscStaveTempoEvent(String destination, int tempo, long creationTime) {
        return new OscStaveTempoEvent(createJavaScriptArgs(), destination, tempo, creationTime);
    }

    public OscStopEvent createOscStopEvent(String destination, long creationTime) {
        return new OscStopEvent(createJavaScriptArgs(), destination, creationTime);
    }

    public PrecountBeatSetupEvent createPrecountBeatSetupEvent(boolean isPrecount, int precountBeatNo, long precountTimeMillis, long initBeaterInterval, Id transportId, long creationTime) {
        return new PrecountBeatSetupEvent(isPrecount, precountBeatNo, precountTimeMillis, initBeaterInterval, transportId, creationTime);
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

    public ElementSelectedEvent createElementSelectedEvent(String elementId, boolean isSelected, String eventId, String sourceAddr, String requestPath,
                                                           long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        return new ElementSelectedEvent(elementId, isSelected, sourceAddr, requestPath, eventId, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public UpdateWebConnectionsEvent createUpdateWebConnectionsEvent(Set<WebConnection> clientConnections, long creationTime) {
        return new UpdateWebConnectionsEvent(clientConnections, creationTime);
    }

    public WebPollEvent createWebPollEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        return new WebPollEvent(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public WebStartEvent createWebStartEvent(String eventId, String sourceAddr, String requestPath,
                                                           long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        return new WebStartEvent(sourceAddr, requestPath, eventId, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public WebScoreEvent createWebScoreEvent(BeatId beatId, List<WebScoreScript> scripts, long creationTime) {
        return new WebScoreEvent(beatId, scripts, creationTime);
    }

    public WebScoreResetEvent createWebScoreResetEvent(BeatId beatId, List<WebScoreScript> scripts, long creationTime) {
        return new WebScoreResetEvent(beatId, scripts, creationTime);
    }

    public OutgoingWebEvent createOutgoingWebEvent(BeatId beatId, String eventId, OutgoingWebEventType eventType, long creationTime) {
        return new OutgoingWebEvent(beatId, eventId, eventType, creationTime);
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
