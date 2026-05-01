package ru.equalizationofgeodeticnetworks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GrossError {
    private final String measurementName;
    private final double residual;
    private final double limit;

    @Override
    public String toString() {
        return String.format("Измерение '%s': невязка = %.6f > %.6f", measurementName, residual, limit);
    }
}
