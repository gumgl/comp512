package rmi;

import java.io.Serializable;

public class Response implements Serializable {
    private Object returnValue;
    private RuntimeException exception = null;

    public Response(Object returnValue, RuntimeException e) {
        this.returnValue = returnValue;
        this.exception = e;
    }
    public Response(Object returnValue) {
        this.returnValue = returnValue;
    }

    /* Static constructor to bundle Exception within Response */
    public static Response error(RuntimeException e) {
        return new Response(null, e);
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public RuntimeException getException() {
        return exception;
    }

    @Override
    public String toString() { return (returnValue == null ? "null" : returnValue.toString());}

}
