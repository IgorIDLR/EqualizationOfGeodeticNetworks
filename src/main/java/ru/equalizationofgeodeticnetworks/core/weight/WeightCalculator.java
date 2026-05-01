package ru.equalizationofgeodeticnetworks.core.weight;

public interface WeightCalculator {
    double computeDistanceWeight(double length, double sigma);
    double computeAngleWeight(double angleDeg, double sigma);
}
