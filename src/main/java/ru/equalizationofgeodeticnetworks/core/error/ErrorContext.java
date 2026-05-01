package ru.equalizationofgeodeticnetworks.core.error;


import ru.equalizationofgeodeticnetworks.model.GrossError;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;
import lombok.Getter;
import java.util.List;

@Getter
public class ErrorContext {

    private final List<Measurement> measurements;
    private final double[] X;
    private final double[][] Q;
    private final List<GrossError> grossErrors;
    public ErrorContext(List<Measurement> measurements, double[] X, double[][] Q, List<GrossError> grossErrors) {
        this.measurements = measurements;
        this.X = X;
        this.Q = Q;
        this.grossErrors = grossErrors;
    }
}
