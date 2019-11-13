package com.xenaksys.szcore.model;

import com.xenaksys.szcore.model.id.BeatId;

public interface Script extends Identifiable, Comparable<Script> {

    BeatId getBeatId();

    String getScript();

}
