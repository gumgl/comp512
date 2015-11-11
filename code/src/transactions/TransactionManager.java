package transactions;

import system.ResourceManager;

import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
    private int increment = 1;

    private final ConcurrentHashMap<Integer, Transaction> transactions =
            new ConcurrentHashMap<Integer, Transaction>();

    /* Add a ResourceManager to the list of involved RMs */
    public boolean enlist(int id, ResourceManager rm) {
        if (! transactions.containsKey(id))
            return false;
        else
            return transactions.get(id).enlist(rm);
    }

    /* Start a new transaction and return its id */
    public synchronized int start() {
        Transaction transaction = new Transaction(increment);
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
}
