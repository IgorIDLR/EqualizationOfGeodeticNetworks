package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.adjustment")
@Data
public class AdjustmentProperties {
    private double eps = 1e-4;
    private int maxIter = 20;
    private boolean useRobust = false;
    private String robustMethod = "HUBER";
    private boolean useSparse = true;
}
