package main.java.PaymentChecksHandler.ChecksHandler.utils;

import org.apache.logging.log4j.Logger;

public final class LogMessage {

    private static final ThreadLocal<String> LOG_MESSAGE = new ThreadLocal<>();

    private LogMessage() {
        // Prevent instantiation
    }

    public static void setLogMessagePrefix(final String prefix) {
        LOG_MESSAGE.set(prefix != null ? prefix + " : " : "");
    }

    public static void close() {
        LOG_MESSAGE.remove();
    }

    public static void log(final Logger logger, final Object message) {
        logger.info(formatMessage(message));
    }

    public static void warn(final Logger logger, final Object message) {
        logger.warn(formatMessage(message));
    }

    public static void debug(final Logger logger, final Object message) {
        logger.debug(formatMessage(message));
    }

    public static void logException(final Logger logger, final Exception exception) {
        logger.error(formatMessage(exception.getMessage()), exception);
    }

    private static String formatMessage(Object message) {
        return String.format("%s%s", LOG_MESSAGE.get(), message);
    }
}
