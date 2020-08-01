package com.xenaksys.szcore.model;

import com.xenaksys.szcore.score.InscorePageMap;

import java.util.Collection;

public interface Page extends Identifiable, Comparable<Page> {

    String getPageName();

    int getPageNo();

    Collection<Bar> getBars();

    Bar getFirstBar();

    Bar getLastBar();

    Beat getFirstBeat();

    Beat getLastBeat();

    Id getInstrumentId();

    String getFileName();

    Id getScoreId();

    InscorePageMap getInscorePageMap();
}
