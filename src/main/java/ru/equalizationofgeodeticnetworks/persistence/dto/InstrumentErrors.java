package ru.equalizationofgeodeticnetworks.persistence.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PolygonometryErrors.class, name = "polygonometry"),
        @JsonSubTypes.Type(value = LevellingErrors.class, name = "levelling")
})
@Data
public abstract class InstrumentErrors { }
