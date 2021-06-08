package com.xenaksys.szcore.model;

public interface Transport extends Identifiable {

    void start();

    void stop();

    void init();

    void init(long position);

    boolean isRunning();

    Tempo getTempo();

    TimeSignature getTimeSignature();

    NoteDuration getBeatPublishResolution();

    long getBaseBeatIntervalMillis();

    long getTempoBeatIntervalMillis();

    long getTickIntervalMillis();

    boolean isPublishClockTick();

    long getPositionMillis();

    int getBeatNo();

    void setTempo(Tempo tempo);

    void setTimeSignature(TimeSignature timeSignature);

    void setBeatPublishResolution(NoteDuration noteDuration);

    void setPublishClockTick(boolean isPublish);

    void setNumberOfTicksPerBeat(int numberOfTicksPerBeat);

    int getNumberOfTicksPerBeat();

    void setBeatNo(int beatNo);

    void setTickNo(int tickNo);

    void onSystemTick();

    void addListener(TransportListener listener);

    long getStartPositionMillis();

    void notifyListenersOnPositionChange(int beatNo);

    public int getCurrentBeatDuration();
}
