package transactions;

import rmi.Buffer;
import rmi.Invocation;
import rmi.Receiver;
import rmi.SocketSender;
import server.RMIResourceManager;
import system.IResourceManager;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Use Locks and LockManager instead of synchronized
public class Transaction {
    private final TransactionManager TM;
    private final int id;
    private State state = State.STARTED;
    private ArrayList<IResourceManager> enlistedRMs = new ArrayList<IResourceManager>();
    private Buffer buffer;
    public final IResourceManager rm;

    public Transaction(TransactionManager TM, int id) {
        this.TM = TM;
        this.id = id;
        this.buffer = new Buffer(); // Store Invocations in this buffer
        this.rm = new RMIResourceManager(this.buffer); // Have a IResourceManager that stores into our buffer
        // MiddleWareResourceManager then calls operations on our rm
    }

    /* Add a ResourceManager to the list of involved RMs */
    public boolean enlist(IResourceManager rm) {
        if (state != State.STARTED)
            return false;
        else if (enlistedRMs.contains(rm))
            return false;
        else {
            enlistedRMs.add(rm);
            return true;
        }
    }

    /*public void addOperation(Invocation operation) {
        synchronized (buffer) {
            operations.add(operation);
        }
    }*/

    /* Attempt to commit the given transaction; return true upon success */
    public boolean commit() {
        synchronized (buffer) {
            if (state == State.STARTED) {
                //List<Receiver> receivers = enlistedRMs.stream().map(s -> new Receiver(new Socket(), s)).collect(Collectors.toList());
                for (Invocation operation : buffer.getInvocations()) {
                    operation.setParam(0, -1); // Set transactionId to -1 for direct (non-buffered) invocation
                    Receiver middleware = new Receiver(null, TM.middleware);
                    middleware.handleRMI(operation);
                }
                state = State.COMMITTED;
                return true;
            } else
                return false;
        }
    }

    /* Abort the given transaction */
    public synchronized boolean abort() {
        if (state == State.STARTED) {
            state = State.ABORTED;
            return true;
        } else
            return false;
    }

    public int getId() {
        return id;
    }
    public State getState() {
        return state;
    }

    public enum State {
        STARTED,
        COMMITTED,
        ABORTED
    }
}
