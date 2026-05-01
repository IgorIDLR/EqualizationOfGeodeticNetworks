package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.graph")
@Data
public class GraphOptimizationProperties {
    private boolean enabled = true;
}
