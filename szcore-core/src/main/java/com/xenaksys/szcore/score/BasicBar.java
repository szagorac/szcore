package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.id.BarId;
import com.xenaksys.szcore.model.id.PageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class BasicBar implements Bar {
    static final Logger LOG = LoggerFactory.getLogger(BasicBar.class);

    private final BarId id;
    private final String name;
    private final Tempo tempo;
    private final TimeSignature timeSignature;

    List<Beat> beats = new ArrayList<>();


    public BasicBar(BarId id, String name, Tempo tempo, TimeSignature timeSignature) {
        this.id = id;
        this.name = name;
        this.tempo = tempo;
        this.timeSignature = timeSignature;
    }

    public BasicBar(BarId id, Tempo tempo, TimeSignature timeSignature) {
        this(id, (Consts.DEFAULT_PAGE_PREFIX + id.getBarNo()), tempo, timeSignature);
    }

    @Override
    public int getBarNo() {
        return id.getBarNo();
    }

    @Override
    public String getBarName() {
        return name;
    }

    @Override
    public Id getPageId() {
        return id.getPageId();
    }

    @Override
    public Id getInstrumentId() {
        return id.getInstrumentId();
    }

    @Override
    public Id getScoreId() {
        return id.getScoreId();
    }

    @Override
    public Tempo getTempo() {
        return tempo;
    }

    @Override
    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    @Override
    public Collection<Beat> getBeats() {
        return beats;
    }

    @Override
    public boolean isUpbeatBar() {
        Collection<Beat> beats = getBeats();
        if(beats == null || beats.size() != 1){
            return false;
        }

        return  beats.iterator().next().isUpbeat() ;
    }

    @Override
    public Id getId() {
        return id;
    }

    public void addBeat(Beat beat) {
        if (beats.contains(beat)) {
            LOG.warn("Bar already contains beat" + beat + ", replacing");
            beats.remove(beat);
        }
        beats.add(beat);
        Collections.sort(beats);
    }

    public void addBeat(Beat beat, int position) {
        beats.add(position, beat);
    }

    @Override
    public int compareTo(Bar o) {
        int result = this.getBarNo() - o.getBarNo();
        if (result == 0) {
            PageId pageId = (PageId) this.getPageId();
            PageId oPageId = (PageId) o.getPageId();
            result = pageId.getPageNo() - oPageId.getPageNo();
        }
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicBar)) return false;

        BasicBar basicBar = (BasicBar) o;

        return id.equals(basicBar.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "BasicBar{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tempo=" + tempo +
                ", timeSignature=" + timeSignature +
                '}';
    }
}
