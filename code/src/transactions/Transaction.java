package transactions;

import sockets.RMI;
import sockets.RMIReceiver;
import system.ResourceManager;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Use Locks and LockManager instead of synchronized
public class Transaction {
    private int id;
    private State state = State.STARTED;
    private ArrayList<ResourceManager> enlistedRMs = new ArrayList<ResourceManager>();
    private ArrayList<RMI> operations = new ArrayList<RMI>();

    public Transaction(int id) {
        this.id = id;
    }

    /* Add a ResourceManager to the list of involved RMs */
    public boolean enlist(ResourceManager rm) {
        if (state != State.STARTED)
            return false;
        else if (enlistedRMs.contains(rm))
            return false;
        else {
            enlistedRMs.add(rm);
            return true;
        }
    }

    public void addOperation(RMI operation) {
        synchronized (operations) {
            operations.add(operation);
        }
    }

    /* Attempt to commit the given transaction; return true upon success */
    public boolean commit() {
        synchronized (operations) {
            List<RMIReceiver> receivers = enlistedRMs.stream().map(s -> new RMIReceiver(new Socket(), s)).collect(Collectors.toList());
            for (RMI operation : operations) {
                for (RMIReceiver receiver : receivers) { // TODO: Not correct, must filter based on item type
                    // One possible solution is to have a special case id=-1 where MiddleWareServer will not consider transactions.
                    receiver.handleRMI(operation);
                }
            }
            state = State.COMMITTED;
            return false;
        }
    }

    /* Abort the given transaction */
    public synchronized boolean abort() {
        state = State.ABORTED;
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
