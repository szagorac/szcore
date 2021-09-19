package com.xenaksys.szcore.util;


import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static final long MEGABYTE = 1024L * 1024L;

    public static double calculateY(double x, PolynomialSplineFunction func) {
        return func.value(x);
    }

    public static PolynomialSplineFunction getFitFunction(double[] x, double[] y) {
        SplineInterpolator interpolator = new SplineInterpolator();
        return interpolator.interpolate(x, y);
    }

    public static double roundTo5DecimalPlaces(double value) {
        return (double) Math.round(value * 100000d) / 100000d;
    }

    public static double roundTo2DecimalPlaces(double value) {
        return (double) Math.round(value * 100d) / 100d;
    }


    public static double bytesToMbyte(long bytes) {
        return 1.0 * bytes / MEGABYTE;
    }

    public static long percentile(long[] values, int perc) {
        Arrays.sort(values);
        return percentileSorted(values, perc);
    }

    public static long percentileSorted(long[] sortedArray, int perc) {
        int ind = (int) Math.ceil((perc / 100.0) * (double) sortedArray.length);
        return sortedArray[ind - 1];
    }

    public static double percentile(List<Double> values, double perc) {
        Collections.sort(values);
        int index = (int) Math.ceil((perc / 100) * values.size());
        return values.get(index - 1);
    }

    public static long median(long[] values) {
        Arrays.sort(values);
        int len = values.length;
        int half = len / 2;
        if (len % 2 == 0) {
            return (values[half] + values[half - 1]) / 2;
        } else {
            return values[half];
        }
    }

    public static long mean(long[] values) {
        Arrays.sort(values);
        return meanSorted(values);
    }

    public static long meanSorted(long[] sortedValues) {
        long sum = 0L;
        int len = sortedValues.length;
        for (long v : sortedValues) {
            sum += v;
        }
        return sum / len;
    }

    public static double percentile(double[] values) {
        Percentile percentile = new Percentile();
        return percentile.evaluate(values);
    }

    public static double median(double[] values) {
        Median median = new Median();
        return median.evaluate(values);
    }

    public static double mean(double[] values) {
        Mean median = new Mean();
        return median.evaluate(values);
    }

    public static int getRandomInRange(int rangeStart, int rangeEnd) {
        return ThreadLocalRandom.current().nextInt(rangeStart, rangeEnd + 1);
    }

    public static double[] mode(double[] values) {
        return StatUtils.mode(values);
    }

    public static long min(long[] values) {
        Arrays.sort(values);
        return minSorted(values);
    }

    public static long minSorted(long[] sortedValues) {
        return sortedValues[0];
    }
}
