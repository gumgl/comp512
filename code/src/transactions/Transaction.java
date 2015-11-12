package transactions;

import rmi.Buffer;
import rmi.Invocation;
import rmi.Receiver;
import server.RMIResourceManager;
import system.IResourceManager;
import system.RMHashtable;
import system.RMItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Use Locks and LockManager instead of synchronized
public class Transaction {
    protected final int id;
    protected State state = State.STARTED;
    protected final HashSet<IResourceManager> enlistedRMs = new HashSet<IResourceManager>();
    //private Buffer buffer;
    /* Local copy of the items */
    public final RMHashtable cacheValues = new RMHashtable();
    /* What objects have been modified */
    public final HashSet<String> cacheChanged = new HashSet<String>();

    public Transaction(int id) {
        this.id = id;
        //this.buffer = new Buffer(); // Store Invocations in this buffer
        //this.rm = new RMIResourceManager(this.buffer); // Have a IResourceManager that stores into our buffer
        // MiddlewareResourceManager then calls operations on our rm
    }

    /* Return whether this item has been seen in this transaction */
    public boolean isAwareOf(String key) {
        // Either it is cached, or it was removed but is still in cacheChanged
        return cacheValues.containsKey(key) || cacheChanged.contains(key);
    }

    /*public void addOperation(Invocation operation) {
        synchronized (buffer) {
            operations.add(operation);
        }
    }*/

    /*public State getState() { // Made item public
        return state;
    }*/

    public enum State {
        STARTED,
        COMMITTED,
        ABORTED
    }
}
