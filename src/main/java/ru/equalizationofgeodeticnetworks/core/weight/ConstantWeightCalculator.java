package ru.equalizationofgeodeticnetworks.core.weight;

public class ConstantWeightCalculator implements WeightCalculator {
    private final double distWeight, angleWeight;
    public ConstantWeightCalculator(double distWeight, double angleWeight) {
        this.distWeight = distWeight;
        this.angleWeight = angleWeight;
    }
    @Override
    public double computeDistanceWeight(double length, double sigma) { return distWeight; }
    @Override
    public double computeAngleWeight(double angleDeg, double sigma) { return angleWeight; }
}