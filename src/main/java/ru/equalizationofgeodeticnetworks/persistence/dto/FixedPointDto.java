package ru.equalizationofgeodeticnetworks.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FixedPointDto {
    @NotBlank private String name;
    @NotNull private Double x;
    @NotNull private Double y;
    @NotNull private Double height;
}
