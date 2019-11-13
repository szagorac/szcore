package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.util.TimeUtil;

public class ErrorEvent extends ClientEvent{

    private final String error;
    private final String source;
    private final Exception exception;


    public ErrorEvent(String error, String source, Exception exception, long time) {
        super(time);
        this.error = error;
        this.source = source;
        this.exception = exception;
    }

    public String getError() {
        return error;
    }

    public String getSource() {
        return source;
    }

    public Exception getException() {
        return exception;
    }

    public String getExceptionMessage() {
        if(exception == null){
            return Consts.NAME_NA;
        }
        String message = exception.getMessage();
        if(message == null){
            Throwable t = exception.getCause();
            if(t != null) {
                message = t.getMessage();
            }
        }
        return (message==null)?Consts.NAME_NA:message;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.ERROR;
    }

    @Override
    public String toString() {
        return "ErrorEvent{" +
                " time='" + TimeUtil.formatTime(getCreationTime()) + '\'' +
                ", error='" + error + '\'' +
                ", exception='" + getExceptionMessage() + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
