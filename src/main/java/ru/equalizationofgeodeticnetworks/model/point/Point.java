package ru.equalizationofgeodeticnetworks.model.point;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Point {

    private final TypePoint type;
    private final double x;
    private final double y;
    private final double height;

    public Point(double x, double y) {
        this.type = TypePoint.PLANAR;
        this.x = x;
        this.y = y;
        this.height = Double.NaN;
    }

    public Point(double height) {
        this.type = TypePoint.LEVEL;
        this.x = Double.NaN;
        this.y = Double.NaN;
        this.height = height;
    }

    @Override
    public String toString() {
        return type == TypePoint.PLANAR ? String.format("(%.3f, %.3f)", x, y) : String.format("(%.3f)", height);
    }
}