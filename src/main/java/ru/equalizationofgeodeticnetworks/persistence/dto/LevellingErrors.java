package ru.equalizationofgeodeticnetworks.persistence.dto;

import lombok.Data;

@Data
public class LevellingErrors extends InstrumentErrors {
    private Double heightDifferenceErrorM;
}
