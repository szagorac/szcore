package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestPageSort {
    @Test
    public void testSort(){
        Id instrumentId = new StrId("Instrument1");
        Id scoreId = new StrId("Score 1");

        Page page1 = new BasicPage(new PageId(1, instrumentId, scoreId), null);
        Page page2 = new BasicPage(new PageId(2, instrumentId, scoreId), null);
        Page page5 = new BasicPage(new PageId(5, instrumentId, scoreId), null);
        Page page10 = new BasicPage(new PageId(10, instrumentId, scoreId), null);

        List<Page> pages = new ArrayList<>();
        pages.add(page10);
        pages.add(page5);
        pages.add(page1);
        pages.add(page2);

        Collections.sort(pages);

        Assert.assertEquals(pages.get(0), page1);
        Assert.assertEquals(pages.get(1), page2);
        Assert.assertEquals(pages.get(2), page5);
        Assert.assertEquals(pages.get(3), page10);

    }
}
