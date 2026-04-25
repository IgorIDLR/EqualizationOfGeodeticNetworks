package ru.equalizationofgeodeticnetworks.point;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Point extends AbstractPoint {

    private double x, y;

    public Point(double x, double y) {
        super(null); this.x = x; this.y = y;
    }
    public Point(String name, double x, double y) {
        super(name);
        this.x = x; this.y = y;
    }
}
