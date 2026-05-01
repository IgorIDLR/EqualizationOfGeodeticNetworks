package ru.equalizationofgeodeticnetworks.persistence.dto;

import lombok.Data;

@Data
public class PolygonometryErrors extends InstrumentErrors {
    private Double angleErrorSec;
    private Double distanceErrorM;
}
