package ru.equalizationofgeodeticnetworks.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DistanceMeasurementDto extends MeasurementDto {
    @NotBlank private String from;
    @NotBlank private String to;
    @NotNull @Positive private Double valueM;
}
