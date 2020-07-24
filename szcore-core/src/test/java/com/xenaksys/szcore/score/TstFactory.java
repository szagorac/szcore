package com.xenaksys.szcore.score;

import com.xenaksys.szcore.instrument.BasicInstrument;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;

public class TstFactory {

    public static Instrument createInstrument(String name, boolean isAv) {
        Id id = new StrId(name);
        return new BasicInstrument(id, name, isAv);
    }

    public static PageId createPageId(int pageNo, Id instrumentId, Id scoreId) {
        return new PageId(pageNo, instrumentId, scoreId);
    }

    public static Page createPage(int pageNo, Id instrumentId, Id scoreId, String fileName) {
        PageId id = createPageId(pageNo, instrumentId, scoreId);
        return new BasicPage(id, fileName);
    }
}
