package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.PageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BasicPage implements Page {
    static final Logger LOG = LoggerFactory.getLogger(BasicPage.class);

    private final PageId id;
    private final String name;
    private final String fileName;
    private final InscorePageMap inscorePageMap;
    private final boolean isSendInscoreMap;

    List<Bar> bars = new ArrayList<>();

    public BasicPage(PageId id, String fileName) {
        this(id, Consts.DEFAULT_PAGE_PREFIX + id.getPageNo(), fileName);
    }

    public BasicPage(PageId id, String name, String fileName) {
        this(id, name, fileName, null);
    }

    public BasicPage(PageId id, String name, String fileName, InscorePageMap inscorePageMap) {
        this(id, name, fileName, inscorePageMap, false);
    }

    public BasicPage(PageId id, String name, String fileName, InscorePageMap inscorePageMap, boolean isSendInscoreMap) {
        this.id = id;
        this.fileName = fileName;
        this.name = name;
        this.inscorePageMap = inscorePageMap;
        this.isSendInscoreMap = isSendInscoreMap;
    }

    @Override
    public String getPageName() {
        return name;
    }

    @Override
    public int getPageNo() {
        return id.getPageNo();
    }

    @Override
    public Collection<Bar> getBars() {
        return bars;
    }

    @Override
    public Bar getFirstBar() {
        if (bars == null || bars.isEmpty()) {
            return null;
        }
        return bars.get(0);
    }

    @Override
    public Bar getLastBar() {
        if (bars == null || bars.isEmpty()) {
            return null;
        }
        return bars.get(bars.size() - 1);
    }

    @Override
    public Beat getFirstBeat() {
        Bar first = getFirstBar();
        if (first == null) {
            return null;
        }
        return first.getFirstBeat();
    }

    @Override
    public Beat getLastBeat() {
        Bar last = getLastBar();
        if (last == null) {
            return null;
        }
        return last.getLastBeat();
    }

    @Override
    public Id getInstrumentId() {
        return id.getInstrumentId();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public Id getScoreId() {
        return id.getScoreId();
    }

    public InscorePageMap getInscorePageMap() {
        return inscorePageMap;
    }

    @Override
    public long getDurationMs() {
        Beat firstBeat = getFirstBeat();
        Beat lastBeat = getLastBeat();

        long start = firstBeat.getStartTimeMillis();
        long end = lastBeat.getEndTimeMillis();
        return end - start;
    }

    public boolean isSendInscoreMap() {
        return isSendInscoreMap;
    }

    @Override
    public Id getId() {
        return id;
    }

    public void addBar(Bar bar) {
        if (bars.contains(bar)) {
            LOG.warn("Page already contains bar: " + bar + ", replacing");
            bars.remove(bar);
        }
        bars.add(bar);
        Collections.sort(bars);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicPage)) return false;

        BasicPage basicPage = (BasicPage) o;

        return id.equals(basicPage.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public int compareTo(Page o) {
        return this.getPageNo() - o.getPageNo();
    }

    @Override
    public String toString() {
        return "BasicPage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
