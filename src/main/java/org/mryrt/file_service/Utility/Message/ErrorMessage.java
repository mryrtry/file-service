package org.mryrt.file_service.Utility.Message;

import org.springframework.http.HttpStatus;

public interface ErrorMessage {

    String getFormattedMessage(Object... args);

    String getErrorField();

    HttpStatus getHttpStatus();

}
