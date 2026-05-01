package ru.equalizationofgeodeticnetworks.persistence.dto;

import java.util.List;

public interface PointNamesContainer {
    List<FixedPointDto> getFixedPoints();
    List<UnknownPointDto> getUnknownPoints();
}