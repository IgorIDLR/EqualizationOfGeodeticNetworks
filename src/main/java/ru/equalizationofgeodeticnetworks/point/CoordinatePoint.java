package ru.equalizationofgeodeticnetworks.point;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class CoordinatePoint extends Point {

    private double X;
    private double Y;

    public CoordinatePoint() {};
    public CoordinatePoint(String name, double X, double Y) {
        super(name); this.X = X; this.Y = Y;
    }

    @Override public String toString() {
        return String.format("Point: %s = [ X: %f, Y: %f ]", this.name, this.X, this.Y);
    }

    @Override public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override public int hashCode() { return super.hashCode(); }
}
