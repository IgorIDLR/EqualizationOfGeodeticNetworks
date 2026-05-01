package ru.equalizationofgeodeticnetworks.utils.converter;

public record AngleDms(int degrees, int minutes, double seconds) {

    public double toDecimal() {
        return degrees + minutes / 60.0 + seconds / 3600.0;
    }

    public static AngleDms fromDecimal(double decimalDegrees) {
        int degrees = (int) decimalDegrees;
        double remainder = (decimalDegrees - degrees) * 60;
        int minutes = (int) remainder;
        double seconds = (remainder - minutes) * 60;
        return new AngleDms(degrees, minutes, seconds);
    }
}
