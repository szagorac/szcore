package com.xenaksys.szcore.util;


import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class MathUtil {

    public static double calculateY(double x, PolynomialSplineFunction func){
       return func.value(x);
    }

    public static PolynomialSplineFunction getFitFunction(double[] x, double[] y){
        SplineInterpolator interpolator = new SplineInterpolator();
        return interpolator.interpolate(x, y);
    }

    public static double roundTo5DecimalPlaces(double value){
        return (double)Math.round(value * 100000d) / 100000d;
    }

}
