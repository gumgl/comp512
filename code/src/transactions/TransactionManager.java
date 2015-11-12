package transactions;

import locks.DeadlockException;
import locks.LockManager;
import server.Trace;
import system.ResourceManager;
import system.RMHashtable;
import system.RMItem;
import transactions.Transaction.State;

import java.util.concurrent.ConcurrentHashMap;

/* Manages the transactions and global storage for an RM.
 * It is accessed by multiple threads concurrently, therefore everything needs to be thread-safe! */
public class TransactionManager {
    /* Transaction Counter */
    private int TC = 1;
    public final RMHashtable items = new RMHashtable();
    private final ConcurrentHashMap<Integer, Transaction> transactions =
            new ConcurrentHashMap<Integer, Transaction>();
    protected LockManager LM = new LockManager();

    protected Transaction getTransaction(int id) {
        return transactions.get(id);
    }
    /*public ResourceManager getTransactionBuffer(int id) {
        return transactions.get(id).rm;
    }*/

    /* Add a ResourceManager to the list of involved RMs */
    public boolean enlist(int tid, ResourceManager rm) {
        Transaction t = getTransaction(tid);
        if (t == null || t.state != State.STARTED)
            return false;
        else {
            t.enlistedRMs.add(rm);
            return true;
        }
    }

    /* Read an RMItem within a transaction */
    public RMItem read(int tid, String key) throws DeadlockException {
        synchronized(items) {
            Transaction t = getTransaction(tid);
            if (t == null)
                return null;
            if (!t.isAwareOf(key)) { // Item not seen before
                Trace.info(String.format("Read new item %s for T%d", key, tid));
                RMItem value = (RMItem) items.get(key); // Get from RM

                if (value != null) { // If item is in global storage
                    Trace.info("Requesting READ lock...");
                    this.LM.Lock(tid, key, LockManager.READ); // Request READ lock
                    Trace.info("READ Lock received! Cached item.");
                    t.cacheValues.put(key, value); // Cache it
                    return value;
                } // Otherwise do not request a lock on a non-existent item
                return value;
            } else {
                Trace.info("Got item from RM storage");
                return (RMItem) t.cacheValues.get(key); // Return cached copy
            }
        }
    }

    /* Write an RMItem within a transaction */
    public void write(int tid, String key, RMItem value) throws DeadlockException {
        synchronized(items) {
            Transaction t = getTransaction(tid);

            if (t == null)
                return;
            if (!t.isAwareOf(key)) { // Item not seen before
                Trace.info(String.format("Write new item %s for T%d", key, tid));
                Trace.info("Requesting WRITE lock...");
                this.LM.Lock(tid, key, LockManager.WRITE); // Request WRITE lock
                Trace.info("WRITE Lock received!");
                t.cacheChanged.add(key); // Remember that we've changed the item
            }

            Trace.info("Wrote item to cache");
            t.cacheValues.put(key, value); // Cache it
        }
    }

    /* Start a new transaction and with a new ID */
    public synchronized int start() {
        Transaction transaction = start(TC);
        TC++;
        return transaction.id;
    }
    /* Start a transaction with a given ID */
    public Transaction start(int tid) {
        synchronized (transactions) {
            if (transactions.containsKey(tid)) {
                return null; // TODO use Exceptions
            } else {
                Transaction t = new Transaction(tid);
                transactions.put(tid, t);
                return t;
            }
        }
    }

    /* Attempt to commit the given transaction; return true upon success */
    public boolean commit(int tid) throws Exception {
        Transaction t = transactions.get(tid);
        synchronized (t) {
            if (t == null || t.state != State.STARTED) // Transaction does not exist or not in correct state
                return false;
            else synchronized (items) {
                for (String key : t.cacheChanged) // Store all changes to global storage
                    items.put(key, t.cacheValues.get(key));

                LM.UnlockAll(tid); // Release all locks held locally
                t.state = State.COMMITTED;
                // Notify all enlisted RMs to commit
                // Might take a long time while we have a lock on t but that's fine since we're ending t
                for (ResourceManager rm : t.enlistedRMs)
                    rm.commit(tid);
                return true;
            }
        }
    }

    // TODO: Refactor to use Exceptions try/catch/finally
    /* Abort the given transaction */
    public boolean abort(int tid) throws Exception {
        Transaction t = transactions.get(tid);
        synchronized (t) {
            if (t == null || t.state != State.STARTED) // Transaction does not exist or not in correct state
                return false;
            else {
                t.state = State.ABORTED;
                for (ResourceManager rm : t.enlistedRMs)
                    rm.abort(tid);
                return true;
            }
        }
    }

    public boolean isTransactionIdValid(int tid) {
        Transaction t = transactions.get(tid);
        return t != null && t.state == State.STARTED;
    }
}
