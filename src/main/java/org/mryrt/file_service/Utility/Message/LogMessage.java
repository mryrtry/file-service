package org.mryrt.file_service.Utility.Message;

import io.micrometer.common.util.internal.logging.InternalLogLevel;
import org.slf4j.Logger;

public interface LogMessage {

    String getMessageTemplate();

    InternalLogLevel getLogLevel();

    Logger getLogger();

    default void log(Object... args) {
        String logMessage = String.format(getMessageTemplate(), args);
        Logger logger = getLogger();
        switch (getLogLevel()) {
            case WARN -> logger.warn(logMessage);
            case ERROR -> logger.error(logMessage);
            case DEBUG -> logger.debug(logMessage);
            case INFO -> logger.info(logMessage);
            case TRACE -> logger.trace(logMessage);
            default -> throw new IllegalArgumentException("Unsupported log level: %s".formatted(getLogLevel()));
        }
    }

}
