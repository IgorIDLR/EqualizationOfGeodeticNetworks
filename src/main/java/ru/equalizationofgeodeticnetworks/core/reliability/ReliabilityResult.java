package ru.equalizationofgeodeticnetworks.core.reliability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReliabilityResult {
    private final double[] redundancy;
    private final List<Double> mdb;
    private final double unitWeightError;
}