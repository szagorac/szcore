package com.xenaksys.szcore.score;

import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.util.MathUtil;
import org.junit.Assert;
import org.junit.Test;

import static com.xenaksys.szcore.Consts.DYNAMICS_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.DYNAMICS_LINE_Y_MIN;

public class TestValueScaler {

    @Test
    public void testScaleValueSimple(){
        double minInput = 0.0;
        double maxInput = 100.0;
        double minOutput = 0.0;
        double maxOutput = 1000.0;
        ValueScaler vt = new ValueScaler(minInput, maxInput, minOutput, maxOutput);

        double out = vt.scaleValue(50.0);
        Assert.assertEquals(500.0, out, 10E-5);

        out = vt.scaleValue(10.0);
        Assert.assertEquals(100.0, out, 10E-5);

        out = vt.scaleValue(75.0);
        Assert.assertEquals(750.0, out, 10E-5);

        out = vt.scaleValue(0);
        Assert.assertEquals(0.0, out, 10E-5);

        out = vt.scaleValue(100.0);
        Assert.assertEquals(1000.0, out, 10E-5);

        out = vt.scaleValue(-12.0);
        Assert.assertEquals(0.0, out, 10E-5);

        out = vt.scaleValue(101.0);
        Assert.assertEquals(1000.0, out, 10E-5);
    }

    @Test
    public void testScaleValueToSmallRange(){
        double minInput = 0.0;
        double maxInput = 100.0;
        double minOutput = DYNAMICS_LINE_Y_MIN;
        double maxOutput = DYNAMICS_LINE_Y_MAX;
        ValueScaler vt = new ValueScaler(minInput, maxInput, minOutput, maxOutput);

        double out = vt.scaleValue(50.0);
        Assert.assertEquals(0.037, out, 10E-5);

        out = vt.scaleValue(75.0);
        Assert.assertEquals( 0.0555, out, 10E-5);

        out = vt.scaleValue(10L);
        Assert.assertEquals( 0.0074, out, 10E-5);
    }

    @Test
    public void testRounding(){
        double out = 0.0370000001;
        out = MathUtil.roundTo5DecimalPlaces(out);
        Assert.assertEquals(0.037, out, 10E-5);

        out = 0.0369999999999;
        out = MathUtil.roundTo5DecimalPlaces(out);
        Assert.assertEquals(0.037, out, 10E-5);
    }

    @Test
    public void testScaleInvertedRange(){
        double minInput = 0.0;
        double maxInput = 100.0;
        double minOutput = 255.0;
        double maxOutput = 0.0;
        ValueScaler vt = new ValueScaler(minInput, maxInput, minOutput, maxOutput);

        double out = vt.scaleValue(50.0);
        Assert.assertEquals(127.5, out, 10E-5);

        out = vt.scaleValue(0.0);
        Assert.assertEquals(255.0, out, 10E-5);

        out = vt.scaleValue(100.0);
        Assert.assertEquals(0.0, out, 10E-5);

        out = vt.scaleValue(90L);
        Assert.assertEquals( 25.5, out, 10E-5);

        out = vt.scaleValue(10L);
        Assert.assertEquals( 229.5, out, 10E-5);
    }
}
