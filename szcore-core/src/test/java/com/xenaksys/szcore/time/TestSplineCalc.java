package com.xenaksys.szcore.time;


import com.xenaksys.szcore.score.BeatFollowerPositionStrategy;
import com.xenaksys.szcore.util.MathUtil;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class TestSplineCalc {
    static final Logger LOG = LoggerFactory.getLogger(TestSplineCalc.class);

    @Test
    public void testPreSchedule(){
        double[] x = {0.0, 6.0, 12.0, 24.0};
        double[] y = {0.0, 10.0, 10.0, 0.0};

        PolynomialSplineFunction func = MathUtil.getFitFunction(x, y);

        double[] xOut = new double[24];
        double[] yOut = new double[24];

        for(int i = 0; i < 24; i++){
            double node = 1.0*i;
            xOut[i] = node;
            yOut[i] = func.value(node);
        }
        LOG.info("x: " + Arrays.toString(xOut));
        LOG.info("y: " + Arrays.toString(yOut));
    }

    @Test
    public void testBeatStrategy() {
        BeatFollowerPositionStrategy strategy = new BeatFollowerPositionStrategy();
        int out = strategy.getYPercent(0);
        Assert.assertEquals(0, out);

        out = strategy.getYPercent(25);
        Assert.assertEquals(100, out);

        out = strategy.getYPercent(50);
        Assert.assertEquals(80, out);

        out = strategy.getYPercent(100);
        Assert.assertEquals(0, out);

        double[] xOut = new double[100];
        double[] yOut = new double[100];
        for(int i = 0; i < 100; i++){
            xOut[i] = 1.0*i;
            yOut[i] = strategy.getYPercent(i);
        }
        LOG.info("x: " + Arrays.toString(xOut));
        LOG.info("y: " + Arrays.toString(yOut));
    }

}
