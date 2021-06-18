package com.xenaksys.szcore.util;

import com.xenaksys.szcore.model.HistoBucket;
import com.xenaksys.szcore.model.HistoBucketView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Histogram {
    private SimpleDateFormat HISTO_SDF = new SimpleDateFormat("HH:mm:ss");
    private long TOLERANCE_MS = 10;

    private final int maxBucketsNo;
    private final long bucketPeriodMs;
    private final long maxDurationMs;

    private LinkedList<HistoBucket> buckets = new LinkedList<>();

    public Histogram(int maxBucketsNo, long bucketPeriodMs) {
        this.maxBucketsNo = maxBucketsNo;
        this.bucketPeriodMs = bucketPeriodMs;
        this.maxDurationMs = bucketPeriodMs * maxBucketsNo;
    }

    public int hit(long now) {
        HistoBucket bucket = buckets.size() > 0 ? buckets.getLast() : null;
        if (bucket == null || now >= bucket.getStartTimeMs() + bucketPeriodMs) {
            buckets.add(bucket = new HistoBucket(now));
        }
        int count = bucket.incrementCount();
        cleanUp(now);
        return count;
    }

    public int getTotalHitCount(long now) {
        cleanUp(now);
        int total = 0;
        for (HistoBucket bucket : buckets) {
            total += bucket.getCount();
        }
        return total;
    }

    private void cleanUp(long now) {
        while (buckets.size() > 0 && buckets.getFirst().getStartTimeMs() <= (now - maxDurationMs)) {
            buckets.removeFirst();
        }
    }

    public int getMaxBucketsNo() {
        return maxBucketsNo;
    }

    public long getBucketPeriodMs() {
        return bucketPeriodMs;
    }

    public long getMaxDurationMs() {
        return maxDurationMs;
    }

    public LinkedList<HistoBucket> getBuckets(long now) {
        cleanUp(now);
        return buckets;
    }

    public List<HistoBucketView> getBucketViews(long now) {
        LinkedList<HistoBucket> buckets = getBuckets(now);
        long histoStartTime = now - maxBucketsNo * bucketPeriodMs;
        List<HistoBucketView> out = new ArrayList<>();

        for (int i = 0; i < maxBucketsNo; i++) {
            long bucketStartTimeMs = histoStartTime + i * bucketPeriodMs;
            long bucketEndTimeMs = bucketStartTimeMs + bucketPeriodMs - 1;
            String dateLabel = HISTO_SDF.format(new Date(bucketEndTimeMs));
            int count = 0;
            HistoBucket bucket = findBucket(bucketStartTimeMs - TOLERANCE_MS, bucketEndTimeMs, buckets);
            if (bucket != null) {
                count = bucket.getCount();
            }
            HistoBucketView bucketView = new HistoBucketView(bucketStartTimeMs, count, dateLabel);
            out.add(bucketView);
        }
        return out;
    }

    private HistoBucket findBucket(long startTime, long endTime, LinkedList<HistoBucket> buckets) {
        for (HistoBucket bucket : buckets) {
            long bucketTime = bucket.getStartTimeMs();
            if (bucketTime >= startTime && bucketTime < endTime) {
                return bucket;
            }
        }
        return null;
    }
}
