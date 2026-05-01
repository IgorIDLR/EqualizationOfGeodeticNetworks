package ru.equalizationofgeodeticnetworks.core.error;

public abstract class ErrorHandler {
    protected final ErrorHandler next;
    protected ErrorHandler(ErrorHandler next) { this.next = next; }
    public abstract void handle(ErrorContext context);
    protected void handleNext(ErrorContext context) {
        if (next != null) next.handle(context);
    }
}
