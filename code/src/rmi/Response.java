package rmi;

import java.io.Serializable;

public class Response implements Serializable {
    private Object returnValue;
    private Exception exception = null;

    public static Response error(Exception e) {
        return new Response(false, e);
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() { return returnValue.toString();}

    public Response(Object returnValue, Exception e) {
        this.returnValue = returnValue;
    }
    public Response(Object returnValue) {
        this.returnValue = returnValue;
    }
}
