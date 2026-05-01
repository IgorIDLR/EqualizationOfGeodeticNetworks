package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.reliability")
@Data
public class ReliabilityProperties {
    private double alpha = 0.05;
    private double beta = 0.80;
    private boolean lambdaCalculated = false;
    private double lambdaFixed = 12.0;
}
