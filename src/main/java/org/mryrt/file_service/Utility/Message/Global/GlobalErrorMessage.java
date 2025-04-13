package org.mryrt.file_service.Utility.Message.Global;

import lombok.Getter;

@Getter
public enum GlobalErrorMessage {

    INVALID_REQUEST_TYPE("request", "Invalid request type"),
    INVALID_JSON("params", "Invalid JSON format");

    private final String field;

    private final String messageTemplate;

    GlobalErrorMessage(String field, String messageTemplate) {
        this.field = field;
        this.messageTemplate = messageTemplate;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}
