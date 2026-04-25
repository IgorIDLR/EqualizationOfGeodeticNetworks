package ru.equalizationofgeodeticnetworks.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.equalizationofgeodeticnetworks.point.Point;

import java.util.Map;

@Getter @Setter
@AllArgsConstructor
public class ProcessingState {

    private Map<String, Point> known;
    private Double currentDir;
    private String currentStation;
}
