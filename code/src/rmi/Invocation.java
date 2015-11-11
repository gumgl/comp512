package rmi;

import java.io.Serializable;
import java.util.ArrayList;

public class Invocation implements Serializable {
    private String methodName;
    private ArrayList<Object> params;

    public Invocation(String methodName, ArrayList<Object> params) {
        this.methodName = methodName;
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public ArrayList<Object> getParams() {
        return params;
    }

    public Invocation(String methodName) {
        this.methodName = methodName;
        this.params = new ArrayList<Object>();
    }

    public void setParam(int position, Object p) {
        if (params.get(position) != null) {
            params.set(position, p);
        }
    }
    public void addParam(Object p) {
        params.add(p);
    }
    public int getParamCount() {
        return this.params.size();
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
