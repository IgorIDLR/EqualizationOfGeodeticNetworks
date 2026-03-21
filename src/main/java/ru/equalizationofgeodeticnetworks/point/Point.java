package ru.equalizationofgeodeticnetworks.point;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public abstract class Point {

    protected String name;

    public Point() {}
    public Point(String name) { this.name = name;}

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        return !(o instanceof Point p) ?
                false : Objects.equals(this.name, p.name);
    }

    @Override public int hashCode() { return Objects.hash(this.name); }
}
