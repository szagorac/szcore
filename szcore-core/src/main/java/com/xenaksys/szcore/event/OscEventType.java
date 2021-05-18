package com.xenaksys.szcore.event;

public enum OscEventType {
    STAVE_CLOCK_TICK, STAVE_DATE_TICK, DATE_TICK, STAVE_START_MARK, STAVE_TEMPO, STOP, STAVE_TICK_DY, STAVE_Y_POSITION,
    ELEMENT_Y_POSITION, ELEMENT_ALPHA, ELEMENT_COLOR, STAVE_ACTIVATE, PRECOUNT_BEAT_ON, PRECOUNT_BEAT_OFF,
    HELLO, SERVER_HELLO, PING, TRANSITION, ADD_PARTS, INSTRUMENT_SLOTS, INSTRUMENT_RESET_SLOTS, SERVER_IP_BROADCAST, SET_TITLE, SET_PART, RESET_SCORE, RESET_INSTRUMENT,
    RESET_STAVES, BEAT_SCRIPT, OSC_SCRIPT, GENERIC
}
