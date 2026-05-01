package ru.equalizationofgeodeticnetworks.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geodesy.free-network")
@Data
public class FreeNetworkProperties {
    private String fixationType = "FIX_CENTROID";
}
