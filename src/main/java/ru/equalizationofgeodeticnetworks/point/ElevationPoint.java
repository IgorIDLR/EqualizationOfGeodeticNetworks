package ru.equalizationofgeodeticnetworks.point;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс реализации {@code abstract Point}. Хранит информацию для реализации уравнительных схем в высотном представлении.
 * Поле {@code elevation} - хранит информацию отметки точки.
 */
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
