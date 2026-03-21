package ru.equalizationofgeodeticnetworks.point;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ElevationPoint extends Point {

    private double elevation;

    public ElevationPoint() {};
    public ElevationPoint(String name, double elevation) {
        super(name);
        this.elevation = elevation;
    }

    @Override public String toString() {
        return String.format("Point: %s = [ H: %f ]", this.name, this.elevation);
    }

    @Override public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override public int hashCode() { return super.hashCode(); }
}
