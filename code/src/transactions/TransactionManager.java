package transactions;

import middleware.MiddleWareResourceManager;
import system.IResourceManager;

import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
    protected final MiddleWareResourceManager middleware;
    private int increment = 1;

    private final ConcurrentHashMap<Integer, Transaction> transactions =
            new ConcurrentHashMap<Integer, Transaction>();

    public TransactionManager(MiddleWareResourceManager middleware) {
        this.middleware = middleware;
    }

    public Transaction getTransaction(int id) {
        return transactions.get(id);
    }
    public IResourceManager getTransactionBuffer(int id) {
        return transactions.get(id).rm;
    }

    /* Add a IResourceManager to the list of involved RMs */
    public boolean enlist(int id, IResourceManager rm) {
        if (! transactions.containsKey(id))
            return false;
        else
            return transactions.get(id).enlist(rm);
    }

    /* Start a new transaction and return its id */
    public synchronized int start() {
        Transaction transaction = new Transaction(this, increment);
        transactions.put(increment, transaction);
        increment ++;
        return transaction.getId();
    }

    /* Attempt to commit the given transaction; return true upon success */
    public synchronized boolean commit(int id) {
        if (! transactions.containsKey(id))
            return false;
        else
            return transactions.get(id).commit();
    }

    // TODO: Refactor to use Exceptions try/catch/finally
    /* Abort the given transaction */
    public synchronized boolean abort(int id) {
        if (! transactions.containsKey(id))
            return false;
        else {
            Transaction t = transactions.get(id);
            boolean result = t.abort();
            /*if (result)
                transactions.remove(id);*/
            return result;
        }
    }

    public boolean transactionExists(int id) {
        return transactions.containsKey(id);
    }
}
