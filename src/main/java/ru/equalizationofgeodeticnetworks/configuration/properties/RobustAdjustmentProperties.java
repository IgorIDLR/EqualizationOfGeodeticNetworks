package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.robust")
@Data
public class RobustAdjustmentProperties {
    private double huberC = 1.345;
    private double tukeyC = 4.685;
}
