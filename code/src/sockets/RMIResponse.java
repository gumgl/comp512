package sockets;

import java.io.Serializable;

public class RMIResponse implements Serializable {
    private Object returnValue;
    private Exception exception = null;

    public static RMIResponse error(Exception e) {
        return new RMIResponse(false, e);
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public Exception getException() {
        return exception;
    }

    public RMIResponse(Object returnValue, Exception e) {
        this.returnValue = returnValue;
    }
    public RMIResponse(Object returnValue) {
        this.returnValue = returnValue;
    }
}
