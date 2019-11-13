package com.xenaksys.szcore.score;

import com.xenaksys.szcore.util.MathUtil;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class BeatFollowerPositionStrategy {

    private final double[] xPoints = {0.0, 12.5, 25.0, 50.0, 100.0};
    private final double[] yPoints = {0.0, 80.0, 100.0, 80.0, 0.0};
    private final PolynomialSplineFunction func;

    private int[] yPerc = new int[101];

    public BeatFollowerPositionStrategy() {
        this.func = MathUtil.getFitFunction(xPoints, yPoints);
        init();
    }

    private void init() {
        for(int i = 0; i < 101; i++){
            double xPerc = 1.0*i;
            yPerc[i] = (int)Math.round(func.value(xPerc));
        }
    }

    public int getYPercent(int xPercent){
        if( xPercent < 0 || xPercent > 100){
            return 0;
        }

        return yPerc[xPercent];
    }

}
