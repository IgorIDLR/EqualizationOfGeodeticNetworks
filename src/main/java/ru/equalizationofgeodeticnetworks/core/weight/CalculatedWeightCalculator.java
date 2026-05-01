package ru.equalizationofgeodeticnetworks.core.weight;

public class CalculatedWeightCalculator implements WeightCalculator {
    private final double sigmaDistUnit;
    private final double sigmaAngleRad;
    public CalculatedWeightCalculator(double sigmaDistUnit, double sigmaAngleRad) {
        this.sigmaDistUnit = sigmaDistUnit;
        this.sigmaAngleRad = sigmaAngleRad;
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
        return 1.0 / (sigmaAngleRad * sigmaAngleRad);
    }
}
