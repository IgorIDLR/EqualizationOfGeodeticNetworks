package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.weight.polygonometry")
@Data
public class PolygonometryWeightProperties {
    private double angleWeight = 4.25e10;
    private double sigmaDistUnit = 0.02;
}
