package ru.equalizationofgeodeticnetworks.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartEndDirections {
    @NotBlank private String pointName;
    @NotNull private Double valueDeg;
}
