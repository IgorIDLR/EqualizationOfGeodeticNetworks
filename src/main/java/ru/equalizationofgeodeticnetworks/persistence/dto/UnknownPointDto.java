package ru.equalizationofgeodeticnetworks.persistence.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnknownPointDto {
    @NotBlank private String name;
    private Double initialX;
    private Double initialY;
    private Double initialHeight;
}