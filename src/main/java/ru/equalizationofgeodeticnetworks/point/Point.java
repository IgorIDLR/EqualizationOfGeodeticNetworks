package ru.equalizationofgeodeticnetworks.point;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
/**
 * Основной класс для хранения координат точек в декартовом пространстве
 * {@code name} - имя точки. Является основой для реализации методов: {@code equals, hashCode, toString}
 * Ответвления от данного класса требуют хранения <b>своих пространственных данных</b> отвечающих за конкретную реализацию того или иного метода уравнительной схемы ->
 * уравнительная схема для плановых методов, либо для высотных.
 * Данный класс <b>не является</b> основой для хранения ГИС.
 */
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
