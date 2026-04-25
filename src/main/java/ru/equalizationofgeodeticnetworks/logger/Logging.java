package ru.equalizationofgeodeticnetworks.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Logging {

    private static boolean loggingEnabled = true;

    private Logging() {}

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public static void log(Logger logger, Level level, String msg) {
        if (loggingEnabled) {
            logger.log(level, msg);
        }
    }

    public static void log(Logger logger, Level level, String msg, Throwable thrown) {
        if (loggingEnabled) {
            logger.log(level, msg, thrown);
        }
    }
}
