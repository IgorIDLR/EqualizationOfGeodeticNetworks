package ru.equalizationofgeodeticnetworks.core.weight;

public class MixedWeightCalculator implements WeightCalculator {
    private final double angleWeight;
    private final double sigmaDistUnit;
    public MixedWeightCalculator(double angleWeight, double sigmaDistUnit) {
        this.angleWeight = angleWeight;
        this.sigmaDistUnit = sigmaDistUnit;
    }
    @Override
    public double computeDistanceWeight(double length, double sigma) {
        if (sigma > 0) return 1.0 / (sigma * sigma);
        double sigmaDist = sigmaDistUnit * length;
        return 1.0 / (sigmaDist * sigmaDist);
    }
    @Override
    public double computeAngleWeight(double angleDeg, double sigma) {
        if (sigma > 0) return 1.0 / (sigma * sigma);
        return angleWeight;
    }
}