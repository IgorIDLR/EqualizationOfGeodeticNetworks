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
public abstract class AbstractPoint {

    protected String name;

    public AbstractPoint() {}
    public AbstractPoint(String name) { this.name = name;}

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        return !(o instanceof AbstractPoint p) ?
                false : Objects.equals(this.name, p.name);
    }

    @Override public int hashCode() { return Objects.hash(this.name); }
}
