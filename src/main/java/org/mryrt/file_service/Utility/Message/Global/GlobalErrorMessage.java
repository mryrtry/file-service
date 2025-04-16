package org.mryrt.file_service.Utility.Message.Global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Getter
@AllArgsConstructor
public enum GlobalErrorMessage implements ErrorMessage {

    INVALID_REQUEST_TYPE("request", "The request type is invalid. Please ensure the request type matches the expected format."),
    INVALID_JSON("params", "The provided JSON is invalid or malformed. Ensure the JSON structure is correct and all required fields are present."),
    RATE_LIMITED_ENDPOINT("rate-limit", "There have been too many requests lately. Try again after %d seconds.");

    private final String errorField;

    private final String messageTemplate;

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}
