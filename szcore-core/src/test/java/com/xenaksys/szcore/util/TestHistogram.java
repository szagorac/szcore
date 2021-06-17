package com.xenaksys.szcore.util;

import com.xenaksys.szcore.model.HistoBucket;
import com.xenaksys.szcore.model.HistoBucketView;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestHistogram {


    @Before
    public void init() {
    }

    @Test
    public void testTenSecHistoBasic() throws Exception {
        int bucketsNo = 10;
        long bucketPeriodMs = 1000L;
        Histogram histogram = new Histogram(bucketsNo, bucketPeriodMs);

        long now = 1000L;
        assertEquals(0, histogram.getBuckets(now).size());
        int count = histogram.hit(now);
        assertEquals(1, count);

        assertEquals(1, histogram.getTotalHitCount(now));
        assertEquals(1, histogram.getBuckets(now).size());

        now = 1500L;
        count = histogram.hit(now);
        assertEquals(2, count);
        assertEquals(2, histogram.getTotalHitCount(now));
        assertEquals(1, histogram.getBuckets(now).size());

        now = 1800L;
        count = histogram.hit(now);
        assertEquals(3, count);
        assertEquals(3, histogram.getTotalHitCount(now));
        assertEquals(1, histogram.getBuckets(now).size());

        now = 2000L;
        count = histogram.hit(now);
        assertEquals(1, count);
        assertEquals(4, histogram.getTotalHitCount(now));
        LinkedList<HistoBucket> buckets = histogram.getBuckets(now);
        assertEquals(2, buckets.size());
        HistoBucket first = buckets.getFirst();
        assertEquals(3, first.getCount());
        HistoBucket last = buckets.getLast();
        assertEquals(1, last.getCount());

        now = 2500L;
        count = histogram.hit(now);
        assertEquals(2, count);
        assertEquals(5, histogram.getTotalHitCount(now));
        buckets = histogram.getBuckets(now);
        assertEquals(2, buckets.size());
        first = buckets.getFirst();
        assertEquals(3, first.getCount());
        last = buckets.getLast();
        assertEquals(2, last.getCount());

        now = 3000L;
        count = histogram.hit(now);
        assertEquals(1, count);
        assertEquals(6, histogram.getTotalHitCount(now));
        buckets = histogram.getBuckets(now);
        assertEquals(3, buckets.size());
        first = buckets.getFirst();
        assertEquals(3, first.getCount());
        HistoBucket second = buckets.get(1);
        assertEquals(2, second.getCount());
        last = buckets.getLast();
        assertEquals(1, last.getCount());

        now = 11000L;
        count = histogram.hit(now);
        assertEquals(1, count);
        assertEquals(4, histogram.getTotalHitCount(now));
        buckets = histogram.getBuckets(now);
        assertEquals(3, buckets.size());
        first = buckets.getFirst();
        assertEquals(2, first.getCount());
        second = buckets.get(1);
        assertEquals(1, second.getCount());
        last = buckets.getLast();
        assertEquals(1, last.getCount());
    }

    @Test
    public void testTenSecHisto() throws Exception {
        int bucketsNo = 10;
        long bucketPeriodMs = 1000L;
        Histogram histogram = new Histogram(bucketsNo, bucketPeriodMs);

        for (int i = 0; i < 10; i++) {
            long secs = (i + 1) * 1000L;
            for (int j = 0; j < i + 1; j++) {
                long time = secs + j;
                histogram.hit(time);
            }
        }

        long now = 10100L;
        LinkedList<HistoBucket> buckets = histogram.getBuckets(now);
        assertEquals(10, buckets.size());
        assertEquals(55, histogram.getTotalHitCount(now));
        for (int i = 0; i < 10; i++) {
            HistoBucket bucket = buckets.get(i);
            assertEquals(i + 1, bucket.getCount());
        }

        List<HistoBucketView> bucketViews = histogram.getBucketViews(now);
        assertEquals(10, bucketViews.size());
        for (int i = 0; i < 10; i++) {
            HistoBucketView bucketView = bucketViews.get(i);
            assertEquals(i + 1, bucketView.getCount());
        }

        now = 11000L;
        buckets = histogram.getBuckets(now);
        assertEquals(9, buckets.size());
        assertEquals(54, histogram.getTotalHitCount(now));
        for (int i = 0; i < 9; i++) {
            HistoBucket bucket = buckets.get(i);
            assertEquals(i + 2, bucket.getCount());
        }

        bucketViews = histogram.getBucketViews(now);
        assertEquals(10, bucketViews.size());
        for (int i = 1; i < 10; i++) {
            HistoBucketView bucketView = bucketViews.get(i);
            assertEquals(i + 1, bucketView.getCount());
        }
    }
}
