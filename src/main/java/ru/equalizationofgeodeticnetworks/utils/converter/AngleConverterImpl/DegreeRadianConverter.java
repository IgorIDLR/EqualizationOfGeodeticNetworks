package ru.equalizationofgeodeticnetworks.utils.converter.AngleConverterImpl;

import org.springframework.stereotype.Component;
import ru.equalizationofgeodeticnetworks.utils.converter.AngleConverter;

@Component
public class DegreeRadianConverter implements AngleConverter {

    @Override
    public double degreesToRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    @Override
    public double radiansToDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    @Override
    public double degreesToDecimal(double degrees, double minutes, double seconds) {
        return degrees + minutes / 60.0 + seconds / 3600.0;
    }

    @Override
    public double decimalToDegrees(double decimalDegrees) {
        return decimalDegrees;
    }
}
