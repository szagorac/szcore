package com.xenaksys.szcore.time;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.NoteDuration;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoImpl;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.TimeSignatureImpl;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;


public class BasicTransport implements Transport {
    static final Logger LOG = LoggerFactory.getLogger(BasicTransport.class);

    static final int MILLIS_IN_MINUTE = Consts.MILLIS_IN_MINUTE;
    static final int DEFAULT_TEMPO = 120;
    static final int DEFAULT_NUMBER_OFBEATS = 4;
    static final int DEFAULT_NUMBER_OF_TICKS_IN_BEAT = 24;

    private final Id id;
    private final Clock clock;
    private final Scheduler scheduler;
    private final BeatTimeStrategy beatTimeStrategy;

    private volatile boolean isStarted = false;
    private CopyOnWriteArrayList<TransportListener> listeners = new CopyOnWriteArrayList<>();

    private volatile Tempo tempo = new TempoImpl(DEFAULT_TEMPO, NoteDuration.QUARTER);
    private volatile TimeSignature timeSignature = new TimeSignatureImpl(DEFAULT_NUMBER_OFBEATS, NoteDuration.CROTCHET);
    private NoteDuration beatPublishResolution = NoteDuration.EIGHTH;
    private int numberOfTicksInBeat = DEFAULT_NUMBER_OF_TICKS_IN_BEAT;
    private boolean isPublishClockTick;
    private long baseBeatIntervalMillis;
    private long tempoBeatIntervalMillis;
    private long tickIntervalMillis;
    private int beatNo = 0;
    private int tickNo = 0;
    private long nextBeatTime = 0L;
    private long publishBeatTime = 0L;
    private long nextTickTime = 0L;
    private long publishTickTime = 0L;
    private long systemStartTime = 0L;
    private long startPositionMilllis = 0L;
    private volatile boolean isTempoChange;


    public BasicTransport(Id id, Clock clock, Scheduler scheduler, BeatTimeStrategy beatTimeStrategy) {
        this.id = id;
        this.clock = clock;
        this.scheduler = scheduler;
        this.beatTimeStrategy = beatTimeStrategy;
        init();
    }

    public void init() {
        calculatePublishIntervals();
    }

    public void init(long position) {
        calculatePublishIntervals();

        publishTickTime = position;
        nextTickTime = position;
        publishBeatTime = position;
        nextBeatTime = position;
        startPositionMilllis = position;
    }

    @Override
    public void onSystemTick() {
        if (!isStarted) {
            return;
        }
        long elapsedStart = clock.getElapsedTimeMillis();

        if (isTransportTickTime()) {
            tick();
        }

        if (isBeatTime()) {
            beat();
        }

        long elapsedEnd = clock.getElapsedTimeMillis();
        long elapsedDiff = (elapsedEnd - elapsedStart);
        if (elapsedDiff > 0) {
            LOG.warn("Execution took millis: " + elapsedDiff);
        }

    }

    @Override
    public void addListener(TransportListener listener) {
        listeners.add(listener);
    }

    @Override
    public long getStartPositionMillis() {
        return startPositionMilllis;
    }

    private void tick() {
        tickNo++;
//        LOG.debug("######  Tick: " + tickNo);
        long previousPublishTime = publishTickTime;
        publishTick();
        caluclateNextTickTime(previousPublishTime);
    }

    private void beat() {
        beatNo++;
//        LOG.debug("### Beat: " + beatNo);
        long previousPublishTime = publishBeatTime;
        publishBeat();
        publishBeatTime = clock.getElapsedTimeMillis();
        tickNo = 0;
        caluclateNextBeatTime(previousPublishTime);
    }

    public void calculatePublishIntervals() {
        if (tempo == null) {
            LOG.error("NO Tempo set");
            return;
        }

        int bpm = tempo.getBpm();
        NoteDuration tempoNoteDuration = tempo.getBeatDuration();
        tempoBeatIntervalMillis = MILLIS_IN_MINUTE / bpm;
        baseBeatIntervalMillis = calculatePublishIntervalMillis(tempoNoteDuration, tempoBeatIntervalMillis);
        tickIntervalMillis = calculateTickIntervalMillis(tempoBeatIntervalMillis);
    }

    public int getCurrentBeatDuration() {
        if (tempo == null || tempo.getBpm() == 0) {
            LOG.error("NO Tempo set");
            return 0;
        }

        int bpm = tempo.getBpm();
        return MILLIS_IN_MINUTE / bpm;
    }

    private void caluclateNextBeatTime(long previousPublishBeatTime) {
        if (baseBeatIntervalMillis == 0) {
            LOG.error("Invalid beat interval millis");
            nextBeatTime = Long.MAX_VALUE;
            return;
        }

        if (!isStarted) {
            return;
        }

        nextBeatTime = beatTimeStrategy.calculateNextBeatTime(publishBeatTime, previousPublishBeatTime, baseBeatIntervalMillis, isTempoChange);
        if(isTempoChange){
            this.isTempoChange = false;
        }
        //LOG.info("Calculated nextBeatTime: " + nextBeatTime);
    }

    private void caluclateNextTickTime(long previousPublishTickTime) {
        if (baseBeatIntervalMillis == 0) {
            LOG.error("Invalid beat interval millis");
            nextTickTime = Long.MAX_VALUE;
            return;
        }

        if(!isStarted){
            return;
        }

        nextTickTime = beatTimeStrategy.calculateNextBeatTime(publishTickTime, previousPublishTickTime, tickIntervalMillis, isTempoChange);
    }

    private long calculatePublishIntervalMillis(NoteDuration tempoNoteDuration, long millisPerBeat) {
        int numberOfTempoBeatsInWhole = tempoNoteDuration.getNumberOfInWhole();
        int numberOfPublishBeatsInWhole = beatPublishResolution.getNumberOfInWhole();
        double multiplier = 1.0 * numberOfTempoBeatsInWhole / numberOfPublishBeatsInWhole;
        long publishInterval = Math.round(millisPerBeat * multiplier);
        return publishInterval;
    }

    private long calculateTickIntervalMillis(long millisPerBeat) {
        double tickResolution = 1.0 * millisPerBeat / numberOfTicksInBeat;
        return Math.round(tickResolution);
    }

    private void publishTick() {
        //        LOG.info("Publishing tick: ");
        for (TransportListener listener : listeners) {
            listener.onClockTick(beatNo, tickNo);
        }
        publishTickTime = clock.getElapsedTimeMillis();

    }

    private void publishBeat() {
        //        LOG.debug("Publishing base beat: " + beatNo + " ElapsedTimeMillis: " + clock.getElapsedTimeMillis());
        for (TransportListener listener : listeners) {
            listener.onBaseBeat(beatNo);
        }
    }

    public void notifyListenersOnPositionChange(int beatNo) {
//        LOG.debug("Publishing base beat: " + beatNo + " ElapsedTimeMillis: " + clock.getElapsedTimeMillis());
        for (TransportListener listener : listeners) {
            listener.onPositionChange(beatNo);
        }
    }

    private boolean isBeatTime() {
        long playTime = clock.getElapsedTimeMillis();
        long diff = nextBeatTime - playTime;
        if (diff < -2l) {
            LOG.warn("Beat Time late millis : " + -1.0 * diff);
        }
        boolean isBeatTime = diff <= 0L;
        if( playTime > 0) {
        //    LOG.info("Is Beat Time: " + isBeatTime + " playTime: " + playTime + " nextBeatTime: " + nextBeatTime + " diff: " + diff);
        }

        return isBeatTime;
    }

    private boolean isTransportTickTime() {
        long playTime = clock.getElapsedTimeMillis();
        long diff = nextTickTime - playTime;
        if (diff < -2l) {
            LOG.warn("Tick Time late millis : " + -1.0 * diff);
        }

        return diff <= 0l;
    }

    @Override
    public void start() {
        if (isStarted) {
            return;
        }
        systemStartTime = clock.getSystemTimeMillis();
        scheduler.start();

        isStarted = true;
    }

    @Override
    public void stop() {
        if (isStarted) {
            LOG.info("Stopping transport: " + id);
            isStarted = false;
            scheduler.onTransportStopped(id);
        }
    }

    @Override
    public boolean isRunning() {
        return isStarted;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Tempo getTempo() {
        return tempo;
    }

    @Override
    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    public NoteDuration getBeatPublishResolution() {
        return beatPublishResolution;
    }

    public long getBaseBeatIntervalMillis() {
        return baseBeatIntervalMillis;
    }

    @Override
    public long getTempoBeatIntervalMillis() {
        return tempoBeatIntervalMillis;
    }

    @Override
    public long getTickIntervalMillis() {
        return tickIntervalMillis;
    }

    @Override
    public boolean isPublishClockTick() {
        return isPublishClockTick;
    }

    @Override
    public long getPositionMillis() {
        return clock.getElapsedTimeMillis();
    }

    @Override
    public int getBeatNo() {
        return beatNo;
    }

    @Override
    public void setTempo(Tempo tempo) {
        this.tempo = tempo;
        this.isTempoChange = true;
        init();
        caluclateNextTickTime(publishTickTime);
        caluclateNextBeatTime(publishBeatTime);
        for (TransportListener listener : listeners) {
            listener.onTempoChange(tempo);
        }
    }

    @Override
    public void setTimeSignature(TimeSignature timeSignature) {
        this.timeSignature = timeSignature;
        init();
        caluclateNextTickTime(publishTickTime);
        caluclateNextBeatTime(publishBeatTime);
    }

    public void setBeatPublishResolution(NoteDuration noteDuration) {
        this.beatPublishResolution = noteDuration;
    }

    @Override
    public void setPublishClockTick(boolean isPublish) {
        this.isPublishClockTick = isPublish;
    }

    @Override
    public void setNumberOfTicksPerBeat(int numberOfTicksPerBeat) {
        this.numberOfTicksInBeat = numberOfTicksPerBeat;
    }

    @Override
    public int getNumberOfTicksPerBeat() {
        return numberOfTicksInBeat;
    }

    @Override
    public void setBeatNo(int beatNo) {
        this.beatNo = beatNo;
    }

    @Override
    public void setTickNo(int tickNo) {
        this.tickNo = tickNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicTransport)) return false;

        BasicTransport that = (BasicTransport) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "BasicTransport{" +
                "id=" + id +
                '}';
    }
}
