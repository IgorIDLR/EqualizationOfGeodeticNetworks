package ru.equalizationofgeodeticnetworks.core.error;

import lombok.extern.slf4j.Slf4j;
import ru.equalizationofgeodeticnetworks.model.GrossError;

@Slf4j
public class RemoveMeasurementHandler extends ErrorHandler {
    public RemoveMeasurementHandler(ErrorHandler next) { super(next); }
    @Override
    public void handle(ErrorContext context) {
        if (!context.getGrossErrors().isEmpty()) {
            log.info("Удаление {} измерений с грубыми ошибками", context.getGrossErrors().size());
            for (GrossError err : context.getGrossErrors()) {
                context.getMeasurements().removeIf(m -> m.getName().equals(err.getMeasurementName()));
            }
        }
        handleNext(context);
    }
}
