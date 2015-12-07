package transactions;

import locks.DeadlockException;
import locks.LockManager;
import server.Trace;
import system.LocalResourceManager;
import system.ResourceManager;
import system.RMHashtable;
import system.RMItem;
import transactions.Transaction.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/* Manages the transactions and global storage for an RM.
 * It is accessed by multiple threads concurrently, therefore everything needs to be thread-safe! */
public class TransactionManager {
	/* Transaction Counter */
	private int TC = 1;
	public RMHashtable items = new RMHashtable();
	private final ConcurrentHashMap<Integer, Transaction> transactions =
			new ConcurrentHashMap<Integer, Transaction>();
	protected LockManager LM = new LockManager();

	protected Transaction getTransaction(int id) {
		return transactions.get(id);
	}

	public final LocalResourceManager RM;

	public TransactionManager(LocalResourceManager RM) {
		this.RM = RM;
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
		Transaction t = getTransaction(tid);
		if (t == null)
			throw new InvalidTransactionIDException(tid);
		else {
			this.LM.Lock(tid, key, LockManager.READ); // Request READ lock
			synchronized (t) {
				if (!t.isAwareOf(key)) { // Item not seen before
					Trace.info(String.format("Read new item %s for T%d", key, tid));
					// Note: reading an item that does not exist returns null, but it is still a fully-qualified read.
					Trace.info("Requesting READ lock...");
					Trace.info("READ Lock received!");
					synchronized (items) { // we use Java's synchronization for the multiple threads and our LM for the many transactions
						RMItem value = (RMItem) items.get(key); // Copy from RM
						if (value != null) // Otherwise, value is already null
							t.cacheValues.put(key, value.copy()); // Cache it
						return value;
					}
				} else {
					Trace.info("Get cached item");
						return t.cacheValues.get(key); // Return cached copy
				}
			}
		}
	}

	/* Write an RMItem within a transaction */
	public void write(int tid, String key, RMItem value) throws DeadlockException {
		Trace.info(String.format("TM::write(%d,%s,%s)", tid, key, value));
		Transaction t = getTransaction(tid);

		if (t == null)
			throw new InvalidTransactionIDException(tid);
		else {
			this.LM.Lock(tid, key, LockManager.WRITE); // Request WRITE lock
			synchronized (t) {
				if (!t.isAwareOf(key)) { // Item not seen before
					Trace.info(String.format("Write new item %s for T%d", key, tid));
					Trace.info("Requesting WRITE lock...");
					Trace.info("WRITE Lock received!");
					t.cacheChanged.add(key); // Remember that we've changed the item
				}
			}
		}

		Trace.info("Write item to cache");
		synchronized (t) {
			if (value == null)
				t.cacheValues.put(key, null);
			else // Cache the new value
				t.cacheValues.put(key, value.copy());
		}
	}


	private boolean localCommit(Transaction t) {
		Trace.info(String.format("TM: LOCAL Committing transaction %d", t.id));
		synchronized (items) {
			for (String key : t.cacheChanged) { // Store all changes to global storage
				RMItem value = t.cacheValues.get(key);
				if (value == null)
					items.put(key, null);
				else
					items.put(key, value.copy());
			}
			LM.UnlockAll(t.id); // Release all locks held locally
			t.state = State.COMMITTED;
			return true;
		}
	}

	private boolean localPrepareCommit(Transaction t) {
		Trace.info(String.format("TM: Preparing commit for T%d", t.id));
		t.state = State.PREPARED;
		return true;
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
		if (!isTransactionIdValid(tid)) // Transaction does not exist or not in correct state
			throw new InvalidTransactionIDException(tid);
		else synchronized (t) {
			Trace.info(String.format("TM: Committing transaction %d", tid));

			localCommit(t);
			// Notify all enlisted RMs to commit
			// Might take a long time while we have a lock on t but that's fine since we're ending t
			for (ResourceManager rm : t.enlistedRMs)
				if (rm != this.RM) // prevent from calling ourself
					rm.commit(tid);
			return true;
		}
	}

	/* Two-phase commit */
	public boolean commit2PC(int tid) {
		Trace.info(String.format("TM: 2PC transaction %d", tid));
		Transaction t = transactions.get(tid);
		synchronized (t) {
			if (!isTransactionIdValid(tid)) // Transaction does not exist or not in correct state
				throw new InvalidTransactionIDException(tid);
			else synchronized (items) {

				boolean success = true;
				for (ResourceManager rm : t.enlistedRMs) { // Gather votes
					if (rm.isAvailable()) {
						Trace.info(String.format("TM: Commit-req %s-RM for T%d", rm.getName(), tid));
						success &= rm.commitRequest(tid);
						Trace.info(String.format("TM: Received %s from %s-RM for T%d", (success ? "commit" : "abort"), rm.getName(), tid));
					} else{
						success = false;
						Trace.info(String.format("TM: %s-RM not available!", rm.getName()));
						break;
					}
				}

				if (success) { // Apply result of vote
					for (ResourceManager rm : t.enlistedRMs)
						if (rm != this.RM) { // prevent from calling ourself
							Trace.info(String.format("TM: Committing %s-RM for T%d", rm.getName(), tid));
							success &= rm.commitFinish(tid);
						}
				} else {
					for (ResourceManager rm : t.enlistedRMs) {
						Trace.info(String.format("TM: Aborting %s-RM for T%d", rm.getName(), tid));
						success &= rm.abort(tid);
					}
				}

				return true;
			}
		}
	}

	/* Commit the transaction to persistant storage */
	public boolean commitRequest(int tid) {
		Trace.info(String.format("TM: Commit request for T%d", tid));
		Transaction t = transactions.get(tid);
		synchronized (t) {
			if (!isTransactionIdValid(tid)) // Transaction does not exist or not in correct state
				return false;
			else {
				return localPrepareCommit(t);
			}
		}
	}

	/* Finishes the 2PC commit */
	public boolean commitFinish(int tid) {
		Trace.info(String.format("TM: Commit finish for T%d", tid));

		Transaction t = transactions.get(tid);
		synchronized (t) {
			if (!isTransactionIdValidForCommitFinish(tid)) // Transaction does not exist or not in correct state
				throw new InvalidTransactionIDException(tid);
			else {
				return localCommit(t);
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
				Trace.info(String.format("TM: Aborting transaction %d", tid));
				t.state = State.ABORTED;
				t.cacheValues.clear();
				t.cacheChanged.clear();
				LM.UnlockAll(tid); // Release all locks held locally
				for (ResourceManager rm : t.enlistedRMs)
					if (rm != this.RM) // prevent from calling ourself
						rm.abort(tid);
				return true;
			}
		}
	}

	public boolean isTransactionIdValid(int tid) {
		Transaction t = transactions.get(tid);
		return t != null && t.state == State.STARTED;
	}

	public boolean isTransactionIdValidForCommitFinish(int tid) {
		Transaction t = transactions.get(tid);
		return t != null && t.state == State.PREPARED;
	}

	public ArrayList<Transaction> getPendingTransactions() {
		return transactions.values().stream().filter(t -> t.state == State.PREPARED).collect(Collectors.toCollection(ArrayList::new));
	}

	public void loadPendingTransactions(ArrayList<Transaction> txns) {
		for (Transaction t : txns) {
			transactions.put(t.id, t);
		}
	}
}
