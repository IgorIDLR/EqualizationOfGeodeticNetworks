package ru.equalizationofgeodeticnetworks.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AngleMeasurementDto extends MeasurementDto {
    @NotBlank private String station;
    private String back;
    private String forward;
    @NotNull private Double valueDeg;
}
