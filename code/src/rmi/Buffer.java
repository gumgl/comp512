package rmi;

import java.net.InetAddress;
import java.util.ArrayList;

public class Buffer implements Invokable {
    private final ArrayList<Invocation> invocations = new ArrayList<Invocation>();

    public ArrayList<Invocation> getInvocations() {
        return invocations;
    }

    public void clear() {
        invocations.clear();
    }

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        invocations.add(invocation);
        return null;
    }
}
