package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.solver.conjugate-gradient")
@Data
public class ConjugateGradientProperties {
    private int maxIter = 200;
    private double tolerance = 1e-8;
}
