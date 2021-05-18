package com.xenaksys.szcore.algo;

public class ValueScaler {
    private final double minInput;
    private final double maxInput;
    private final double minOutput;
    private final double maxOutput;

    private double inputRange;
    private double outputRange;

    public ValueScaler(double minInput, double maxInput, double minOutput, double maxOutput) {
        this.minInput = minInput;
        this.maxInput = maxInput;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;

        this.inputRange = maxInput - minInput;
        this.outputRange = maxOutput - minOutput;
    }

//
//            (outMax-outMin)(x - inMin)
//    f(x) = -------------------------  + outMin
//                inMax - inMin

    public double scaleValue(double value) {
        if(value < minInput) {
            return minOutput;
        }

        if(value > maxInput) {
            return maxOutput;
        }

        double num = outputRange * (value - minInput);
        double denom = inputRange;

        if(denom == 0.0) {
            return value;
        }

        double frac = num/denom;
        return frac + minOutput;
    }

}
