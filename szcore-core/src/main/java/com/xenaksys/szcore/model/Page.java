package com.xenaksys.szcore.model;

import java.util.Collection;

public interface Page extends Identifiable, Comparable<Page> {

    String getPageName();

    int getPageNo();

    Collection<Bar> getBars();

    Id getInstrumentId();

    String getFileName();

    Id getScoreId();

}
