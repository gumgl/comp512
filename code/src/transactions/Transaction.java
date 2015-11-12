package transactions;

import system.ResourceManager;
import system.RMHashtable;

import java.util.HashSet;

// TODO: Use Locks and LockManager instead of synchronized
public class Transaction {
    protected final int id;
    protected State state = State.STARTED;
    protected final HashSet<ResourceManager> enlistedRMs = new HashSet<ResourceManager>();
    /* Local copy of the items */
    public final RMHashtable cacheValues = new RMHashtable();
    /* What objects have been modified */
    public final HashSet<String> cacheChanged = new HashSet<String>();

    public Transaction(int id) {
        this.id = id;
    }

    /* Return whether this item has been seen in this transaction */
    public boolean isAwareOf(String key) {
        // Either it is cached, or it was removed but is still in cacheChanged
        return cacheValues.containsKey(key) || cacheChanged.contains(key);
    }

    public enum State {
        STARTED,
        COMMITTED,
        ABORTED
    }
}
