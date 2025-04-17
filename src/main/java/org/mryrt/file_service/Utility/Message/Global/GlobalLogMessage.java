package org.mryrt.file_service.Utility.Message.Global;

import io.micrometer.common.util.internal.logging.InternalLogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.LogMessage;
import org.slf4j.Logger;

@AllArgsConstructor
@Getter
@Slf4j
public enum GlobalLogMessage implements LogMessage {

    EXECUTION_TIME(InternalLogLevel.INFO, "Method '%s' was executed in '%s'"),
    GLOBAL_ERROR_OCCURRED(InternalLogLevel.DEBUG, "Global exception occurred with cause: '%s'.");

    private final InternalLogLevel logLevel;

    private final String messageTemplate;

    @Override
    public Logger getLogger() {
        return log;
    }

}