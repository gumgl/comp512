package server;

// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

import rmi.Invocation;
import rmi.Invokable;
import system.RMHashtable;
import system.ResourceManager;

import java.util.Vector;

/*
 * This class will forward all RM calls to an RMI.Invokable and return the result
 */

public class RMIResourceManager extends ResourceManager {

	protected Invokable target;

	public RMIResourceManager(Invokable target){
		this.target = target;
	}

	// Flight operations //

	@Override
	public boolean addFlight(int tid, int flightNumber,
							 int numSeats, int flightPrice) {
		Invocation invocation = new Invocation("addFlight");
		invocation.addParam(tid);
		invocation.addParam(flightNumber);
		invocation.addParam(numSeats);
		invocation.addParam(flightPrice);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean deleteFlight(int tid, int flightNumber) {
		Invocation invocation = new Invocation("deleteFlight");
		invocation.addParam(tid);
		invocation.addParam(flightNumber);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public int queryFlight(int tid, int flightNumber) {
		Invocation invocation = new Invocation("queryFlight");
		invocation.addParam(tid);
		invocation.addParam(flightNumber);
		return (Integer) target.invoke(invocation);
	}

	@Override
	public int queryFlightPrice(int tid, int flightNumber) {
		Invocation invocation = new Invocation("queryFlightPrice");
		invocation.addParam(tid);
		invocation.addParam(flightNumber);
		return (Integer) target.invoke(invocation);
	}

	// Car operations //

	@Override
	public boolean addCars(int tid, String location, int numCars, int carPrice) {
		Invocation invocation = new Invocation("addCars");
		invocation.addParam(tid);
		invocation.addParam(location);
		invocation.addParam(numCars);
		invocation.addParam(carPrice);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean deleteCars(int tid, String location) {
		Invocation invocation = new Invocation("deleteCars");
		invocation.addParam(tid);
		invocation.addParam(location);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public int queryCars(int tid, String location) {
		Invocation invocation = new Invocation("queryCars");
		invocation.addParam(tid);
		invocation.addParam(location);
		return (Integer) target.invoke(invocation);
	}

	@Override
	public int queryCarsPrice(int tid, String location) {
		Invocation invocation = new Invocation("queryCarsPrice");
		invocation.addParam(tid);
		invocation.addParam(location);
		return (Integer) target.invoke(invocation);
	}

	// Room operations //

	@Override
	public boolean addRooms(int tid, String location, int numRooms, int roomPrice) {
		Invocation invocation = new Invocation("addRooms");
		invocation.addParam(tid);
		invocation.addParam(location);
		invocation.addParam(numRooms);
		invocation.addParam(roomPrice);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean deleteRooms(int tid, String location) {
		Invocation invocation = new Invocation("deleteRooms");
		invocation.addParam(tid);
		invocation.addParam(location);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public int queryRooms(int tid, String location) {
		Invocation invocation = new Invocation("queryRooms");
		invocation.addParam(tid);
		invocation.addParam(location);
		return (Integer) target.invoke(invocation);
	}

	@Override
	public int queryRoomsPrice(int tid, String location) {
		Invocation invocation = new Invocation("queryRoomsPrice");
		invocation.addParam(tid);
		invocation.addParam(location);
		return (Integer) target.invoke(invocation);
	}


	// Customer operations //

	@Override
	public int newCustomer(int tid) {
		Invocation invocation = new Invocation("newCustomer");
		invocation.addParam(tid);
		return (Integer) target.invoke(invocation);
	}

	@Override
	public boolean newCustomerId(int tid, int customerId) {
		Invocation invocation = new Invocation("newCustomerId");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean deleteCustomer(int tid, int customerId) {
		Invocation invocation = new Invocation("deleteCustomer");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public RMHashtable getCustomerReservations(int tid, int customerId) {
		Invocation invocation = new Invocation("getCustomerReservations");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		return (RMHashtable) target.invoke(invocation);
	}

	@Override
	public String queryCustomerInfo(int tid, int customerId) {
		Invocation invocation = new Invocation("queryCustomerInfo");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		return target.invoke(invocation).toString();
	}

	@Override
	public boolean reserveFlight(int tid, int customerId, int flightNumber) {
		Invocation invocation = new Invocation("reserveFlight");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		invocation.addParam(flightNumber);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean reserveCar(int tid, int customerId, String location, int numCars) {
		Invocation invocation = new Invocation("reserveCar");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		invocation.addParam(location);
		invocation.addParam(numCars);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean reserveRoom(int tid, int customerId, String location, int numRooms) {
		Invocation invocation = new Invocation("reserveRoom");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		invocation.addParam(location);
		invocation.addParam(numRooms);
		return (Boolean) target.invoke(invocation);
	}


	@Override
	public boolean reserveItinerary(int tid, int customerId, Vector flightNumbers,
	                                String location, int numCars, int numRooms) {
		Invocation invocation = new Invocation("reserveItinerary");
		invocation.addParam(tid);
		invocation.addParam(customerId);
		invocation.addParam(flightNumbers);
		invocation.addParam(location);
		invocation.addParam(numCars);
		invocation.addParam(numRooms);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public int start() {
		Invocation invocation = new Invocation("start");
		return (Integer) target.invoke(invocation);
	}

	@Override
	public boolean start(int transactionId) {
		Invocation invocation = new Invocation("start");
		invocation.addParam(transactionId);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean commit(int transactionId) {
		Invocation invocation = new Invocation("commit");
		invocation.addParam(transactionId);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean commitRequest(int transactionId) {
		Invocation invocation = new Invocation("commitRequest");
		invocation.addParam(transactionId);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean abort(int transactionId) {
		Invocation invocation = new Invocation("abort");
		invocation.addParam(transactionId);
		return (Boolean) target.invoke(invocation);
	}

	@Override
	public boolean shutdown() {
		Invocation invocation = new Invocation("shutdown");
		boolean success = (Boolean) target.invoke(invocation);
		active = !success;
		return success;
	}

}
