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
        Trace.info(String.format("TM::read(%d,%s)", tid, key));
        synchronized(items) {
            Transaction t = getTransaction(tid);
            if (t == null)
                throw new InvalidTransactionIDException(tid);
            else if (!t.isAwareOf(key)) { // Item not seen before
                Trace.info(String.format("Read new item %s for T%d", key, tid));

                // Note: reading an item that does not exist returns null, but it is still a fully-qualified read.
                Trace.info("Requesting READ lock...");
                this.LM.Lock(tid, key, LockManager.READ); // Request READ lock
                Trace.info("READ Lock received!");
                RMItem value = (RMItem) items.get(key); // Copy from RM
                if (value != null) // Otherwise, value is already null
                    t.cacheValues.put(key, value.copy()); // Cache it
                return value;
            } else {
                Trace.info("Get cached item");
                return (RMItem) t.cacheValues.get(key); // Return cached copy
            }
        }
    }

    /* Write an RMItem within a transaction */
    public void write(int tid, String key, RMItem value) throws DeadlockException {
        Trace.info(String.format("TM::write(%d,%s,%s)", tid, key, value));
        synchronized(items) {
            Transaction t = getTransaction(tid);

            if (t == null)
                throw new InvalidTransactionIDException(tid);
            else if (!t.isAwareOf(key)) { // Item not seen before
                Trace.info(String.format("Write new item %s for T%d", key, tid));
                Trace.info("Requesting WRITE lock...");
                this.LM.Lock(tid, key, LockManager.WRITE); // Request WRITE lock
                Trace.info("WRITE Lock received!");
                t.cacheChanged.add(key); // Remember that we've changed the item
            }

            Trace.info("Write item to cache");
            if (value == null)
                t.cacheValues.put(key, null);
            else // Cache the new value
                t.cacheValues.put(key, value.copy());
        }
    }

    /* Start a new transaction and with a new ID */
    public synchronized int start() throws InvalidTransactionIDException {
        Transaction transaction = start(TC);
        TC++;
        return transaction.id;
    }
    /* Start a transaction with a given ID */
    public Transaction start(int tid) throws InvalidTransactionIDException {
        synchronized (transactions) {
            if (transactions.containsKey(tid)) {
                throw new InvalidTransactionIDException(tid);
            } else {
                Transaction t = new Transaction(tid);
                transactions.put(tid, t);
                return t;
            }
        }
    }

    /* Attempt to commit the given transaction; return true upon success */
    public boolean commit(int tid) {
        Transaction t = transactions.get(tid);
        synchronized (t) {
            if (!isTransactionIdValid(tid)) // Transaction does not exist or not in correct state
                throw new InvalidTransactionIDException(tid);
            else synchronized (items) {
                for (String key : t.cacheChanged) { // Store all changes to global storage
                    RMItem value = t.cacheValues.get(key);
                    if (value == null)
                        items.put(key, null);
                    else
                        items.put(key, value.copy());
                }
                LM.UnlockAll(tid); // Release all locks held locally
                t.state = State.COMMITTED;
                // Notify all enlisted RMs to commit
                // Might take a long time while we have a lock on t but that's fine since we're ending t
                for (ResourceManager rm : t.enlistedRMs)
                    rm.commit(tid); // TODO: Possible infinite loop because they will all call each other
                return true;
            }
        }
    }

    /* Abort the given transaction */
    public boolean abort(int tid) {
        Transaction t = transactions.get(tid);
        synchronized (t) {
            if (!isTransactionIdValid(tid)) // Transaction does not exist or not in correct state
                throw new InvalidTransactionIDException(tid);
            else {
                t.state = State.ABORTED;
                t.cacheValues.clear();
                t.cacheChanged.clear();
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
