package ru.equalizationofgeodeticnetworks.core.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdjustmentEventListener {

    @EventListener
    public void handleStart(AdjustmentEvent.Start event) { log.info("Уравнивание начато"); }

    @EventListener
    public void handleIteration(AdjustmentEvent.IterationCompleted event) {
        log.info("Итерация {}: max поправка = {} м", event.getIteration(), event.getMaxDelta());
    }

    @EventListener
    public void handleComplete(AdjustmentEvent.Complete event) {
        log.info("Уравнивание завершено, s0 = {}", event.getStatistics().getUnitWeightError());
    }

    @EventListener
    public void handleError(AdjustmentEvent.Error event) { log.error("Ошибка при уравнивании", event.getException()); }
}
