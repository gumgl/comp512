package sockets;

import java.io.Serializable;
import java.util.ArrayList;

public class RMI implements Serializable {
    private String methodName;
    private ArrayList<Object> params;

    public RMI(String methodName, ArrayList<Object> params) {
        this.methodName = methodName;
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public ArrayList<Object> getParams() {
        return params;
    }

    public RMI(String methodName) {
        this.methodName = methodName;
        this.params = new ArrayList<Object>();
    }

    public void addParam(Object p) {
        params.add(p);
    }

    public String toString() {
        StringBuilder output = new StringBuilder(methodName + "(");
        boolean first = true;
        for (Object param : params) {
            if (first)
                first = false;
            else
                output.append(", ");
            output.append(param.toString());
        }
        output.append(")");
        return output.toString();
    }
}
