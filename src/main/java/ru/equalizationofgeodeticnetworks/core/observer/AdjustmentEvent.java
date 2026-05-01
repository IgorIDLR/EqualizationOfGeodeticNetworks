package ru.equalizationofgeodeticnetworks.core.observer;

import ru.equalizationofgeodeticnetworks.statistics.AdjustmentStatistics;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public abstract class AdjustmentEvent extends ApplicationEvent {
    protected AdjustmentEvent(Object source) { super(source); }
    @Getter public static class Start extends AdjustmentEvent { public Start(Object source) { super(source); } }
    @Getter public static class Complete extends AdjustmentEvent {
        private final AdjustmentStatistics statistics;
        public Complete(Object source, AdjustmentStatistics statistics) { super(source); this.statistics = statistics; }
    }
    @Getter public static class Error extends AdjustmentEvent {
        private final Exception exception;
        public Error(Object source, Exception exception) { super(source); this.exception = exception; }
    }
    @Getter public static class IterationCompleted extends AdjustmentEvent {
        private final int iteration;
        private final double maxDelta;
        public IterationCompleted(Object source, int iteration, double maxDelta) { super(source); this.iteration = iteration; this.maxDelta = maxDelta; }
    }
}
