package ru.equalizationofgeodeticnetworks.persistence.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AngleMeasurementDto.class, name = "angle"),
        @JsonSubTypes.Type(value = DistanceMeasurementDto.class, name = "distance"),
        @JsonSubTypes.Type(value = HeightDifferenceDto.class, name = "height")
})
@Data
public abstract class MeasurementDto {
    @Positive private Double individualSigma;
}
