package ru.equalizationofgeodeticnetworks.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HeightDifferenceDto extends MeasurementDto {
    @NotBlank private String from;
    @NotBlank private String to;
    @NotNull private Double valueM;
}