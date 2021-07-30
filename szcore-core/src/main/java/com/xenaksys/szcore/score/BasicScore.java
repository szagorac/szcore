package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategy;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategyConfig;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.MutableBeatId;
import com.xenaksys.szcore.model.id.MutablePageId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.time.BasicTransport;
import gnu.trove.map.TIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;


public class BasicScore implements Score {
    static final Logger LOG = LoggerFactory.getLogger(BasicScore.class);

    private final StrId id;

    private List<SzcoreEvent> initEvents = new ArrayList<>();
    private Set<Id> transportIds = new HashSet<>();
    private List<Instrument> scoreInstruments = new ArrayList<>();
    private List<Instrument> avInstruments = new ArrayList<>();
    private Map<Id, Instrument> instruments = new HashMap<>();
    private Map<Id, Page> pages = new HashMap<>();
    private Map<Id, Bar> bars = new HashMap<>();
    private Map<Id, Beat> beats = new HashMap<>();
    private Map<Id, List<Script>> beatScripts = new HashMap<>();
    private Map<Id, List<BeatId>> instrumentBeats = new HashMap<>();
    private Map<Id, Id> instrumentTransports = new HashMap<>();
    private Map<Id, List<Id>> transportInstruments = new HashMap<>();
    private Map<Id, Transport> transports = new HashMap<>();
    private Map<Id, TransportContext> transportSpecificData = new HashMap<>();
    private Map<Id, Long> beatToTimeMap = new HashMap<>();
    private Map<Long, List<Id>> timeToBeatMap = new HashMap<>();
    private Map<Id, OSCPortOut> instrumentOscPortMap = new HashMap<>();
    private Map<Id, Stave> staves = new HashMap<>();
    private Map<Id, List<Stave>> instrumentStaves = new HashMap<>();
    private Map<Id, Instrument> oscPlayers = new HashMap<>();

    private boolean isPrecount = true;
    private int precountBeatNo = 4;
    private int precountMillis = 5 * 1000;
    private int precountBeaterInterval = 250;

    private final MutablePageId tempPageId = new MutablePageId(0, null, null);
    private final MutableBeatId tempBeatId = new MutableBeatId(0, null, null, null, null, 0);

    private Page blankPage;
    private Map<Id, Page> instrumentContinuousPage = new HashMap<>();
    private Map<Id, Page> instrumentEndPage = new HashMap<>();

    private boolean isUseContinuousPage = false;
    public int noContinuousPages = 10;
    private boolean isRandomizeContinuousPageContent = true;

    private ScoreRandomisationStrategyConfig randomisationStrategyConfig;
    private ScoreRandomisationStrategy randomisationStrategy;

    private String workingDir;

    public BasicScore(StrId id) {
        this.id = id;
    }

    public void initRandomisation() {
        if (randomisationStrategyConfig == null) {
            LOG.info("initRandomisation: no config, ignoring ...");
            return;
        }
        randomisationStrategy = new ScoreRandomisationStrategy(this, randomisationStrategyConfig);
        randomisationStrategy.init();
    }

    public void setRandomisationStrategy(List<Integer> strategy) {
        if (randomisationStrategy == null || strategy == null) {
            return;
        }

        randomisationStrategy.setAssignmentStrategy(strategy);
    }

    public boolean isRandomisePage(Page nextPage) {
        int pageNo = nextPage.getPageNo();
        boolean out = pageNo > 3;
        LOG.info("isRandomisePage: {} {} ", out, nextPage);
        return out;
    }

    public boolean isUseContinuousPage() {
        return isUseContinuousPage;
    }

    public void setUseContinuousPage(boolean useContinuousPage) {
        isUseContinuousPage = useContinuousPage;
    }

    public boolean isRandomizeContinuousPageContent() {
        return isRandomizeContinuousPageContent;
    }

    public void setRandomizeContinuousPageContent(boolean randomizeContinuousPageContent) {
        isRandomizeContinuousPageContent = randomizeContinuousPageContent;
    }

    public void addInitEvent(SzcoreEvent initEvent) {
        initEvents.add(initEvent);
    }

    public void addInstrument(Instrument instrument) {
        instruments.put(instrument.getId(), instrument);
        if (instrument.isAv()) {
            avInstruments.add(instrument);
        } else {
            scoreInstruments.add(instrument);
        }
    }

    public Page getBlankPage() {
        return blankPage;
    }

    public void setBlankPage(Page blankPage) {
        this.blankPage = blankPage;
    }

    public Page getContinuousPage(Id instrumentId) {
        return instrumentContinuousPage.get(instrumentId);
    }

    public Page getEndPage(Id instrumentId) {
        return instrumentEndPage.get(instrumentId);
    }

    public void setContinuousPage(Id instrumentId, Page continuousPage) {
        this.instrumentContinuousPage.put(instrumentId, continuousPage);
    }

    public void setEndPage(Id instrumentId, Page endPage) {
        this.instrumentEndPage.put(instrumentId, endPage);
    }

    public void addInstrumentTransport(Instrument instrument, Transport transport) {
        Id instrumentId = instrument.getId();
        Id transportId = transport.getId();
        addTransportId(transportId);
        transports.put(transportId, transport);
        instrumentTransports.put(instrumentId, transportId);
        List<Id> instrumentIds = transportInstruments.computeIfAbsent(transportId, k -> new ArrayList<>());
        if (!instrumentIds.contains(instrumentId)) {
            instrumentIds.add(instrumentId);
        }
    }

    public void addClockTickEvent(Id transportId, SzcoreEvent clockTickEvent) {

        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            transportContext = new TransportContext(transportId);
            transportSpecificData.put(transportId, transportContext);
            addTransportId(transportId);
        }

        transportContext.addClockTickEvent(clockTickEvent);
    }

    public void addOneOffClockTickEvent(Id transportId, SzcoreEvent clockTickEvent) throws Exception {

        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            transportContext = new TransportContext(transportId);
            transportSpecificData.put(transportId, transportContext);
            addTransportId(transportId);
        }

        transportContext.addOneOffClockTickEvent(clockTickEvent);
    }

    public void addClockBaseBeatTickEvent(Id transportId, SzcoreEvent clockBaseBeatEvent) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            transportContext = new TransportContext(transportId);
            transportSpecificData.put(transportId, transportContext);
            addTransportId(transportId);
        }

        transportContext.addClockBaseBeatTickEvent(clockBaseBeatEvent);
    }

    public void addTransportId(Id transportId) {
        transportIds.add(transportId);
    }

    public void addScoreBaseBeatEvent(Id transportId, SzcoreEvent scoreBaseBeatEvent) {

        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            transportContext = new TransportContext(transportId);
            transportSpecificData.put(transportId, transportContext);
            addTransportId(transportId);
        }

        transportContext.addScoreBaseBeatEvent(scoreBaseBeatEvent);
    }

    public void addOneOffBaseBeatEvent(Id transportId, SzcoreEvent scoreBaseBeatEvent) {

        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            transportContext = new TransportContext(transportId);
            transportSpecificData.put(transportId, transportContext);
            addTransportId(transportId);
        }
//LOG.debug("Adding OneOffBaseBeatEvent: " + scoreBaseBeatEvent);
        transportContext.addOneOffBaseBeatEvent(scoreBaseBeatEvent);
    }

    public void addTransportBeatId(Beat beat, Id transportId) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            transportContext = new TransportContext(transportId);
            transportSpecificData.put(transportId, transportContext);
        }
        transportContext.addBeatId(beat.getBeatId());
    }

    @Override
    public List<SzcoreEvent> getInitEvents() {
        return initEvents;
    }

    @Override
    public List<SzcoreEvent> getClockTickEvents(Id transportId) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return null;
        }
        return transportContext.getClockTickEvents();
    }

    @Override
    public LinkedBlockingQueue<SzcoreEvent> getOneOffClockTickEvents(Id transportId) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return null;
        }
        return transportContext.getOneOffClockTickEvents();
    }

    @Override
    public List<SzcoreEvent> getClockBaseBeatEvents(Id transportId) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return null;
        }
        return transportContext.getClockBaseBeatEvents();
    }

    @Override
    public Collection<Id> getTransportIds() {
        return transportIds;
    }

    @Override
    public TIntObjectMap<List<SzcoreEvent>> getScoreBaseBeatEvents(Id transportId) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return null;
        }
        return transportContext.getScoreBaseBeatEvents();
    }

    @Override
    public TIntObjectMap<List<SzcoreEvent>> getOneOffBaseBeatEvents(Id transportId) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return null;
        }
        return transportContext.getOneOffBaseBeatEvents();
    }

    @Override
    public List<SzcoreEvent> getOneOffBaseBeatEvents(Id transportId, int baseBeatNo) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return null;
        }
        return transportContext.getOneOffBaseBeatEvents(baseBeatNo);
    }

    @Override
    public void replaceOneOffBaseBeatEvents(Id transportId, int baseBeatNo, List<SzcoreEvent> events) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return;
        }
        transportContext.replaceOneOffBaseBeatEvents(baseBeatNo, events);
    }

    @Override
    public void removeOneOffBeatEvents(Id transportId, int baseBeatNo) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return;
        }
        transportContext.removeOneOffBaseBeatEvents(baseBeatNo);
    }

    @Override
    public String getName() {
        return id.getName();
    }

    @Override
    public Collection<Instrument> getInstruments() {
        return instruments.values();
    }

    @Override
    public Collection<Instrument> getScoreInstruments() {
        return scoreInstruments;
    }

    @Override
    public Collection<Instrument> getAvInstruments() {
        return avInstruments;
    }

    public Instrument getInstrument(String name) {
        if (name == null) {
            return null;
        }
        Collection<Instrument> instruments = getInstruments();
        for (Instrument instrument : instruments) {
            if (name.equals(instrument.getName())) {
                return instrument;
            }
        }
        return null;
    }

    public Collection<Instrument> getOscPlayers() {
        return oscPlayers.values();
    }

    public void addOscPlayer(Instrument instrument) {
        oscPlayers.put(instrument.getId(), instrument);
    }

    public boolean isOscPlayer(InstrumentId instrumentId) {
        return oscPlayers.containsKey(instrumentId);
    }

    @Override
    public Collection<Page> getPages() {
        return pages.values();
    }

    public void addPage(Page page) {
        if (page == null || page.getId() == null) {
            LOG.error("Invalid page to add: " + page);
            return;
        }
        if (pages.containsKey(page.getId())) {
            LOG.warn("Page Already exists, replacing");
        }

        pages.put(page.getId(), page);
    }

    public Page getLastInstrumentPage(Id instrumentId) {
        TreeSet<Page> out = getInstrumentPages(instrumentId);
        Page last = out.last();
        if (last.getPageNo() == Consts.CONTINUOUS_PAGE_NO) {
            out.remove(last);
            last = out.last();
        }
        return last;
    }

    public TreeSet<Page> getInstrumentPages(Id instrumentId) {
        TreeSet<Page> out = new TreeSet<>();

        for(Id pid : pages.keySet()) {
            PageId pageId = (PageId) pid;
            if(pageId.getInstrumentId().equals(instrumentId)) {
                out.add(pages.get(pageId));
            }
        }
        return out;
    }

    public boolean containsPage(Page page) {
        return pages.containsKey(page.getId());
    }

    public boolean containsPage(PageId pageId) {
        return pages.containsKey(pageId);
    }

    public void addBar(Bar bar) {
        if (bar == null || bar.getId() == null) {
            LOG.error("Invalid Bar to add: " + bar);
            return;
        }
        if (bars.containsKey(bar.getId())) {
            LOG.warn("Bar Already exists, replacing");
        }

        bars.put(bar.getId(), bar);

        Id pageId = bar.getPageId();
        BasicPage page = (BasicPage) pages.get(pageId);
        if (page != null) {
            page.addBar(bar);
        }

    }

    public boolean containsBeat(Beat beat) {
        return beats.containsKey(beat.getId());
    }

    public void addBeat(Beat beat) {
        if (beat == null || beat.getId() == null) {
            LOG.error("Invalid Beat to add: " + beat);
            return;
        }
        if (beats.containsKey(beat.getId())) {
            LOG.warn("Beat Already exists, replacing");
        }

        beats.put(beat.getId(), beat);

        Id barId = beat.getBarId();
        BasicBar bar = (BasicBar) bars.get(barId);
        if (bar != null) {
            bar.addBeat(beat);
        }

        Long time = beat.getStartTimeMillis();
        Id beatId = beat.getBeatId();
        beatToTimeMap.put(beatId, time);
        List<Id> timeBeats = timeToBeatMap.computeIfAbsent(time, k -> new ArrayList<>());
        timeBeats.add(beatId);
        List<BeatId> iBeats = instrumentBeats.computeIfAbsent(beat.getInstrumentId(), k -> new ArrayList<>());
        iBeats.add(beat.getBeatId());
        Collections.sort(iBeats);
    }

    public void addScript(Script script) {
        if (script == null || script.getId() == null || script.getBeatId() == null) {
            LOG.error("Invalid scriptObj to add: " + script);
            return;
        }

        List<Script> bScripts = beatScripts.computeIfAbsent(script.getBeatId(), k -> new ArrayList<>());
        bScripts.add(script);
    }

    public void addInstrumentOscPort(Id instrumentId, OSCPortOut port) {
        instrumentOscPortMap.put(instrumentId, port);
    }

    public void addStave(Stave stave) {
        StaveId id = (StaveId) stave.getId();
        Id instrumentId = id.getInstrumentId();
        staves.put(id, stave);
        List<Stave> iStaves = instrumentStaves.computeIfAbsent(instrumentId, k -> new ArrayList<>());
        iStaves.add(stave);
    }

    public boolean doesNotcontainBar(Bar bar) {
        return !bars.containsKey(bar.getId());
    }

    @Override
    public Collection<Bar> getBars() {
        return bars.values();
    }

    @Override
    public Collection<Beat> getBeats() {
        return beats.values();
    }

    @Override
    public Collection<BeatId> getInstrumentBeatIds(Id instrumentId) {
        return instrumentBeats.get(instrumentId);
    }

    @Override
    public Collection<Stave> getStaves() {
        return staves.values();
    }

    @Override
    public Instrument getInstrument(Id instrumentId) {
        if (instrumentId == null) {
            return null;
        }

        return instruments.get(instrumentId);
    }

    @Override
    public List<Stave> getInstrumentStaves(Id instrumentId) {
        if (instrumentId == null) {
            return null;
        }
        return instrumentStaves.get(instrumentId);
    }

    public Stave getStave(StaveId id) {
        if (id == null) {
            return null;
        }
        return staves.get(id);
    }

    @Override
    public OSCPortOut getInstrumentOscPort(Id instrumentId) {
        if (instrumentId == null) {
            return null;
        }

        return instrumentOscPortMap.get(instrumentId);
    }

    @Override
    public Transport getInstrumentTransport(Id instrumentId) {
        if (instrumentId == null) {
            return null;
        }
        Id transportId = instrumentTransports.get(instrumentId);
        if(transportId == null){
            return null;
        }
        return transports.get(transportId);
    }

    @Override
    public List<Id> getTransportInstrumentIds(Id transportId) {
        if(transportId == null){
            return null;
        }
        return transportInstruments.get(transportId);
    }

    @Override
    public Transport getTransport(Id transportId) {
        return transports.get(transportId);
    }

    @Override
    public Collection<Transport> getTransports() {
        return transports.values();
    }

    @Override
    public Bar getBar(Id id) {
        if (id == null) {
            return null;
        }
        return bars.get(id);
    }

    @Override
    public Beat getBeat(Id id) {
        if (id == null) {
            return null;
        }
        return beats.get(id);
    }

    @Override
    public Page getPage(Id id) {
        if (id == null) {
            return null;
        }
        if(pages.containsKey(id)) {
            return pages.get(id);
        }
        return null;
    }

    @Override
    public long getBeatTime(BeatId beatId) {
        if (beatId == null) {
            return 0L;
        }
        return beatToTimeMap.get(beatId);
    }

    @Override
    public BeatId getBeatForTime(long time, Id transportId) {
        BeatId beatId;

        while (time >= 0) {
            List<Id> timeBeats = timeToBeatMap.get(time);
            if (timeBeats == null) {
                time--;
                continue;
            }

            for(Id id : timeBeats){
                if(id == null){
                    continue;
                }

                beatId = (BeatId) id;
                Id instrumentId = beatId.getInstrumentId();
                Transport transport = getInstrumentTransport(instrumentId);
                if (transportId.equals(transport.getId())) {
                    return beatId;
                }
            }
            time--;
        }

        return null;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicScore)) return false;

        BasicScore that = (BasicScore) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "BasicScore{" +
                "id=" + id +
                '}';
    }

    public String getOscDestination(Id instrumentId) {
        if (instrumentId == null) {
            return null;
        }
        String destination = Consts.DEFAULT_OSC_PORT_NAME;
        Instrument instrument = getInstrument(instrumentId);
        if(instrument != null) {
            destination = instrument.getName();
        }
        return destination;
    }

    @Override
    public Stave getCurrentStave(Id instrumentId) {
        if (instrumentId == null) {
            return null;
        }
        Collection<Stave> staves = getInstrumentStaves(instrumentId);
        if (staves == null) {
            return null;
        }
        Stave first = null;
        for (Stave stave : staves) {
            if (first == null) {
                first = stave;
            }
            if (stave.isActive()) {
                return stave;
            }
        }
        return first;
    }

    @Override
    public Stave getNextStave(Id instrumentId) {
        if (instrumentId == null) {
            return null;
        }
        List<Stave> staves = getInstrumentStaves(instrumentId);
        if(staves == null || staves.isEmpty()) {
            return null;
        }

        Stave active = getCurrentStave(instrumentId);
        int activeIndex = staves.indexOf(active);

        int nextIndex = 0;
        if (activeIndex < (staves.size() - 1)) {
            nextIndex = activeIndex + 1;
        }

        return staves.get(nextIndex);
    }

    public Page getNextPage(PageId pageId) {
        if (pageId == null) {
            return null;
        }
        int pageNo = pageId.getPageNo();
        int nexPageNo = pageNo + 1;
        tempPageId.setInstrumentId(pageId.getInstrumentId());
        tempPageId.setScoreId(pageId.getScoreId());
        tempPageId.setPageNo(nexPageNo);
        Page next = getPage(tempPageId);
        tempPageId.reset();
        return next;
    }



    @Override
    public List<BeatId> getBeatIds(Id transportId, int beatNo) {
        TransportContext transportContext = transportSpecificData.get(transportId);
        if (transportContext == null) {
            return null;
        }
        return transportContext.getBeatIds(beatNo);
    }

    public BeatId getInstrumentBeatIds(Id transportId, Id instrumentId, int beatNo) {
        for (BeatId beatId : getBeatIds(transportId, beatNo)) {
            if (beatId.getInstrumentId().equals(instrumentId)) {
                return beatId;
            }
        }
        return null;
    }

    public BeatId getInstrumentBeat(Id instrumentId, int beatNo) {
        Collection<BeatId> instBeats = getInstrumentBeatIds(instrumentId);
        for(BeatId beatId : instBeats) {
            if(beatId.getBeatNo() == beatNo) {
                return beatId;
            }
        }
        return null;
    }

    @Override
    public List<BeatId> findBeatIds(Id transportId, int beatNo) {
        List<BeatId> beatIds = getBeatIds(transportId, beatNo);
        if (beatIds == null) {
            int temp = beatNo;
            while (beatIds == null) {
                temp--;
                if (temp <= 0) {
                    return null;
                }
                beatIds = getBeatIds(transportId, temp);
            }
        }

        return beatIds;
    }

    @Override
    public boolean isPrecount() {
        return isPrecount;
    }

    @Override
    public int getPrecountBeatNo() {
        return precountBeatNo;
    }

    @Override
    public int getPrecountMillis() {
        return precountMillis;
    }

    @Override
    public long getMaxBeatInterval() {
        long maxInterval = 0;
        for (Transport t : transports.values()) {
            BasicTransport transport = (BasicTransport) t;
            transport.calculatePublishIntervals();
            long transportInterval = transport.getTempoBeatIntervalMillis();
            if (transportInterval > maxInterval) {
                maxInterval = transportInterval;
            }
        }
        return maxInterval;
    }

    @Override
    public long getPrecountBeaterInterval() {
        return precountBeaterInterval;
    }

    @Override
    public Beat getUpbeat(BeatId beatId) {
        if(beatId == null){
            return null;
        }

        int upbeatNo = beatId.getBeatNo() - 1;
        Collection<Beat> beats = getBeats();
        Beat upbeat = null;
        for(Beat beat : beats){
            BeatId bid = beat.getBeatId();
            if(bid.getBeatNo() == upbeatNo
                    && bid.getInstrumentId().equals(beatId.getInstrumentId())
                    && bid.getPageId().equals(beatId.getPageId())
                    && bid.getScoreId().equals(beatId.getScoreId())){
                upbeat = beat;
                break;
            }
        }

        return upbeat;
    }

    @Override
    public List<Script> getBeatScripts(BeatId beatId) {
        return beatScripts.get(beatId);
    }

    @Override
    public void resetOnStop() {
        LOG.info("Reset Score on stop");
        Collection<TransportContext> transportContexts = transportSpecificData.values();
        for (TransportContext tc : transportContexts) {
            tc.resetOnStop();
        }
    }

    public void setIsPrecount(boolean isPrecount) {
        this.isPrecount = isPrecount;
    }

    public void setPrecountBeatNo(int precountBeatNo) {
        this.precountBeatNo = precountBeatNo;
    }

    public void setPrecountMillis(int precountMillis) {
        this.precountMillis = precountMillis;
    }

    public void setPrecount(int minPrecountMillis, int precountBeatNo) {
        long maxBeatInterval = getMaxBeatInterval();
        int precountMillis = minPrecountMillis;
        int precountBeatMillis = precountBeatNo * (int) maxBeatInterval;
        if (precountBeatMillis > precountMillis) {
            precountMillis += (precountBeatMillis - precountMillis);
        }
        setIsPrecount(true);
        setPrecountBeatNo(precountBeatNo);
        setPrecountMillis(precountMillis);
    }

    public Beat getOffsetBeat(BeatId beatId, int beatOffset) {
        if (beatId == null) {
            return null;
        }
        int beatNo = beatId.getBeatNo();
        int nextBeatNo = beatNo + beatOffset;
        Id instrumentId = beatId.getInstrumentId();
        List<BeatId> beatIds = instrumentBeats.get(instrumentId);

        tempBeatId.reset();
        tempBeatId.setBeatNo(nextBeatNo);

        int index = Collections.binarySearch(beatIds, tempBeatId);
        BeatId offsetBeatId = null;
        try {
            offsetBeatId = beatIds.get(index);
        } catch (Exception e) {
            LOG.error("Failed to get offset beat: ", e);
        }
        if (offsetBeatId == null) {
            return null;
        }

        return beats.get(offsetBeatId);
    }

    public ScoreRandomisationStrategy getRandomisationStrategy() {
        return randomisationStrategy;
    }

    public void setRandomisationStrategyConfig(ScoreRandomisationStrategyConfig config) {
        this.randomisationStrategyConfig = config;
    }

    public ScoreRandomisationStrategyConfig getRandomisationStrategyConfig() {
        return randomisationStrategyConfig;
    }
}
