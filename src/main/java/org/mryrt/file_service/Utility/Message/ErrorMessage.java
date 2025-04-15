package org.mryrt.file_service.Utility.Message;

public interface ErrorMessage {

    String getFormattedMessage(Object ... args);

    String getErrorField();

}
