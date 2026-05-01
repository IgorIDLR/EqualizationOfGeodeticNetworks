package ru.equalizationofgeodeticnetworks.core.weight;

public class MixedWeightCalculator2 implements WeightCalculator {
    private final double distWeight;
    private final double sigmaAngleRad;
    public MixedWeightCalculator2(double distWeight, double sigmaAngleRad) {
        this.distWeight = distWeight;
        this.sigmaAngleRad = sigmaAngleRad;
    }
    @Override
    public double computeDistanceWeight(double length, double sigma) {
        if (sigma > 0) return 1.0 / (sigma * sigma);
        return distWeight;
    }
    @Override
    public double computeAngleWeight(double angleDeg, double sigma) {
        if (sigma > 0) return 1.0 / (sigma * sigma);
        return 1.0 / (sigmaAngleRad * sigmaAngleRad);
    }
}