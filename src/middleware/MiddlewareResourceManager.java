package middleware;

import server.Trace;
import system.RMHashtable;
import system.ResourceManager;
import system.LocalResourceManager;
import transactions.InvalidTransactionIDException;
import transactions.TransactionManager;

import java.util.Vector;

/*
 * RM who the dispatches queries into separate specialized RMs
 */
public class MiddlewareResourceManager extends ResourceManager {

	ResourceManager flightRM;
	ResourceManager carRM;
	ResourceManager roomRM;
	LocalResourceManager customerRM;
	TransactionManager TM;
	/*private final ConcurrentHashMap<Integer, Integer> customerIds =
			new ConcurrentHashMap<Integer, Integer>();*/

	public MiddlewareResourceManager(ResourceManager flightRM,
									 ResourceManager carRM,
									 ResourceManager roomRM,
									 LocalResourceManager customerRM/*,
									 TransactionManager tm*/) {
		this.flightRM = flightRM;
		this.carRM = carRM;
		this.roomRM = roomRM;
		this.customerRM = customerRM;

		this.TM = customerRM.TM; // We need it to coordinate transaction IDs
	}

	/* Same logic for all operations.
	 * Returns the correct RM on which the operation should be executed */
	private ResourceManager handleOperation(int tid, ResourceManager relevantRM) throws InvalidTransactionIDException {
		if (!TM.isTransactionIdValid(tid)) {
			InvalidTransactionIDException e = new InvalidTransactionIDException(tid);
			Trace.error(e);
			throw e;
		} else {
			TM.enlist(tid, relevantRM);
			return relevantRM;
		}
	}

	@Override
	public String getName() {
		return "middleware";
	}

	@Override
	public boolean addFlight(int tid, int flightNumber, int numSeats, int flightPrice) {
		return handleOperation(tid, flightRM).addFlight(tid, flightNumber, numSeats, flightPrice);
	}

	@Override
	public boolean deleteFlight(int tid, int flightNumber) {
		return handleOperation(tid, flightRM).deleteFlight(tid, flightNumber);
	}

	@Override
	public int queryFlight(int tid, int flightNumber) {
		return handleOperation(tid, flightRM).queryFlight(tid, flightNumber);
	}

	@Override
	public int queryFlightPrice(int tid, int flightNumber) {
		return handleOperation(tid, flightRM).queryFlightPrice(tid, flightNumber);
	}

	@Override
	public boolean addCars(int tid, String location, int numCars, int carPrice) {
		return handleOperation(tid, carRM).addCars(tid, location, numCars, carPrice);
	}

	@Override
	public boolean deleteCars(int tid, String location) {
		return handleOperation(tid, carRM).deleteCars(tid, location);
	}

	@Override
	public int queryCars(int tid, String location) {
		handleOperation(tid, carRM);
		return handleOperation(tid, carRM).queryCars(tid, location);
	}

	@Override
	public int queryCarsPrice(int tid, String location) {
		return handleOperation(tid, carRM).queryCarsPrice(tid, location);
	}

	@Override
	public boolean addRooms(int tid, String location, int numRooms, int roomPrice) {
		return handleOperation(tid, roomRM).addRooms(tid, location, numRooms, roomPrice);
	}

	@Override
	public boolean deleteRooms(int tid, String location) {
		return handleOperation(tid, roomRM).deleteRooms(tid, location);
	}

	@Override
	public int queryRooms(int tid, String location) {
		return handleOperation(tid, roomRM).queryRooms(tid, location);
	}

	@Override
	public int queryRoomsPrice(int tid, String location) {
		return handleOperation(tid, roomRM).queryRoomsPrice(tid, location);
	}

	@Override
	public int newCustomer(int tid) {
		int customerId  = handleOperation(tid, customerRM).newCustomer(tid);
		handleOperation(tid, flightRM).newCustomerId(tid, customerId);
		handleOperation(tid, carRM).newCustomerId(tid, customerId);
		handleOperation(tid, roomRM).newCustomerId(tid, customerId);
		return customerId;
	}

	@Override
	public boolean newCustomerId(int tid, int customerId) {
		boolean success = true;
		success &= handleOperation(tid, customerRM).newCustomerId(tid, customerId);
		success &= handleOperation(tid, flightRM).newCustomerId(tid, customerId);
		success &= handleOperation(tid, carRM).newCustomerId(tid, customerId);
		success &= handleOperation(tid, roomRM).newCustomerId(tid, customerId);
		return success;
	}

	@Override
	public boolean deleteCustomer(int tid, int customerId) {
		boolean success = true;
		success &= handleOperation(tid, customerRM).deleteCustomer(tid, customerId);
		success &= handleOperation(tid, flightRM).deleteCustomer(tid, customerId);
		success &= handleOperation(tid, carRM).deleteCustomer(tid, customerId);
		success &= handleOperation(tid, roomRM).deleteCustomer(tid, customerId);
		return success;
	}

	@Override
	public RMHashtable getCustomerReservations(int tid, int customerId) {
		RMHashtable toReturn = new RMHashtable();
		RMHashtable toAdd;

		toAdd = handleOperation(tid, customerRM).getCustomerReservations(tid, customerId);
		if (toAdd == null)
			return null;
		else
			toReturn.putAll(toAdd);

		toAdd = handleOperation(tid, flightRM).getCustomerReservations(tid, customerId);
		if (toAdd == null)
			return null;
		else
			toReturn.putAll(toAdd);

		toAdd = handleOperation(tid, carRM).getCustomerReservations(tid, customerId);
		if (toAdd == null)
			return null;
		else
			toReturn.putAll(toAdd);

		toAdd = handleOperation(tid, roomRM).getCustomerReservations(tid, customerId);
		if (toAdd == null)
			return null;
		else
			toReturn.putAll(toAdd);

		return toReturn;
	}

	@Override
	public String queryCustomerInfo(int tid, int customerId) {
		Trace.info("MW::queryCustomerInfo(" + tid + ", " + customerId + ")");
		handleOperation(tid, customerRM);
		if (customerRM.getCustomerReservations(tid, customerId) == null) { // Customer does not exist
			Trace.info("MW::deleteCustomer(" + tid + ", " + customerId +
					") failed: customer doesn't exist.");
			return "";
		} else {
			String s = "";
			try {
				s = s + handleOperation(tid, flightRM).queryCustomerInfo(tid, customerId) + "\n";
				s = s + handleOperation(tid, carRM).queryCustomerInfo(tid, customerId) + "\n";
				s = s + handleOperation(tid, roomRM).queryCustomerInfo(tid, customerId) + "\n";
			} catch (Exception e) {
				e.printStackTrace();
				return "-1";
			}
			Trace.info("MW::queryCustomerInfo(" + tid + ", " + customerId + "): \n");
			System.out.println(s);
			return s;
		}
	}

	@Override
	public boolean reserveFlight(int tid, int customerId, int flightNumber) {
		return handleOperation(tid, flightRM).reserveFlight(tid, customerId, flightNumber);
	}

	@Override
	public boolean reserveCar(int tid, int customerId, String location, int numCars) {
		return handleOperation(tid, carRM).reserveCar(tid, customerId, location, numCars);
	}

	@Override
	public boolean reserveRoom(int tid, int customerId, String location, int numRooms) {
		return handleOperation(tid, roomRM).reserveRoom(tid, customerId, location, numRooms);
	}

	@Override
	public boolean reserveItinerary(int tid, int customerId, Vector flightNumbers, String location, int numCars, int numRooms) {
		Trace.info("MW::reserveItinerary(" + tid + ", " + customerId + ", ...)");
		handleOperation(tid, customerRM);
		if (customerRM.getCustomerReservations(tid, customerId) == null) { // Customer does not exist
			Trace.info("MW::reserveItinerary(" + tid + ", " + customerId +
					", ...) failed: customer doesn't exist.");
			return false;
		}
		boolean r = true;
		try {
			for (Object o : flightNumbers) {
				int flightNumber = Integer.parseInt((String) o);
				r &= reserveFlight(tid, customerId, flightNumber);
				Trace.info("MW: Reserve flight " + (r ? "OK" : "Failed"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (numCars > 0) {
			try {
				r &= reserveCar(tid, customerId, location, numCars);
				Trace.info("MW: Reserve car " + (r ? "OK" : "Failed"));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		if (numRooms > 0) {
			try {
				r &= reserveRoom(tid, customerId, location, numRooms);
				Trace.info("MW: Reserve room " + (r ? "OK" : "Failed"));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return r;
	}

	@Override
	public int start() {
		Trace.info("MW::start()");
		//int id = TM.start(); // First locally start with latest TC
		//customerRM.start(id);
		int id = customerRM.start();
		flightRM.start(id); // Then forward start with same TC
		carRM.start(id);
		roomRM.start(id);
		return id;
	}

	@Override
	public boolean start(int transactionId) {
		Trace.info(String.format("MW::start(%d)", transactionId));
		return false; // Middleware should not accept a fixed transactionId
		/*if (TM.isTransactionIdValid(transactionId))
			return false;
		else {
			TM.start(transactionId);
			flightRM.start(transactionId);
			carRM.start(transactionId);
			roomRM.start(transactionId);
			return true;
		}*/
	}

	@Override
	public boolean commit(int transactionId) {
		Trace.info(String.format("MW::commit(%d)", transactionId));
		return TM.commit(transactionId); // The TM also takes care of calling t.enlistedTMs.commit()
		// TM then calls commit on the involved RMs
	}

	@Override
	public boolean commit2PC(int transactionId) {
		Trace.info(String.format("MW::commit2PC(%d)", transactionId));
		return TM.commit2PC(transactionId); // The TM also takes care of calling t.enlistedTMs.commit()
		// TM then calls commit on the involved RMs
	}

	@Override
	public boolean commitRequest(int transactionId) {
		Trace.info(String.format("MW::commitRequest(%d)", transactionId));
		return TM.commitRequest(transactionId);
	}

	@Override
	public boolean commitFinish(int transactionId) {
		Trace.info(String.format("MW::commitFinish(%d)", transactionId));
		return TM.commitFinish(transactionId);
	}

	@Override
	public boolean abort(int transactionId) {
		Trace.info(String.format("MW::abort(%d)", transactionId));
		return TM.abort(transactionId);
	}

	@Override
	public boolean shutdown() {
		boolean success = true;
		success &= flightRM.shutdown();
		success &= carRM.shutdown();
		success &= roomRM.shutdown();
		return success;
	}

	@Override
	public boolean isAvailable() {
		return flightRM.isAvailable()
				&& carRM.isAvailable()
				&& roomRM.isAvailable()
				&& customerRM.isAvailable();
	}
}
