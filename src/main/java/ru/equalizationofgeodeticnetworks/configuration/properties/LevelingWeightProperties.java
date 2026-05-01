package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.weight.leveling")
@Data
public class LevelingWeightProperties {
    private double heightWeight = 1.0;
}
