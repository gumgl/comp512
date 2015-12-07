/** 
 * Simplified version from CSE 593, University of Washington.
 *
 * A Distributed System in Java using Web Services.
 * 
 * Failures should be reported via the return value.  For example, 
 * if an operation fails, you should return either false (boolean), 
 * or some error code like -1 (int).
 *
 * If there is a boolean return value and you're not sure how it 
 * would be used in your implementation, ignore it.  I used boolean
 * return values in the interface generously to allow flexibility in 
 * implementation.  But don't forget to return true when the operation
 * has succeeded.
 */

package system;

import java.util.*;
import javax.jws.WebService;


@WebService
public abstract class ResourceManager {

	public enum Type {
		FLIGHT, ROOM, CAR, CUSTOMER, MIDDLEWARE
	}

	/* Whether we should keep interating with this RM */
	public boolean active = true;

	abstract public String getName();

	// Flight operations //

	/* Add seats to a flight.
	 * In general, this will be used to create a new flight, but it should be
	 * possible to add seats to an existing flight.  Adding to an existing
	 * flight should overwrite the current price of the available seats.
	 *
	 * @return success.
	 */
	abstract public boolean addFlight(int tid, int flightNumber, int numSeats, int flightPrice);

	/**
	 * Delete the entire flight.
	 * This implies deletion of this flight and all its seats.  If there is a
	 * reservation on the flight, then the flight cannot be deleted.
	 *
	 * @return success.
	 */
	abstract public boolean deleteFlight(int tid, int flightNumber);

	/* Return the number of empty seats in this flight. */
	abstract public int queryFlight(int tid, int flightNumber);

	/* Return the price of a seat on this flight. */
	abstract public int queryFlightPrice(int tid, int flightNumber);


	// Car operations //

	/* Add cars to a location.
	 * This should look a lot like addFlight, only keyed on a string location
	 * instead of a flight number.
	 */
	abstract public boolean addCars(int tid, String location, int numCars, int carPrice);

	/* Delete all cars from a location.
	 * It should not succeed if there are reservations for this location.
	 */
	abstract public boolean deleteCars(int tid, String location);

	/* Return the number of cars available at this location. */
	abstract public int queryCars(int tid, String location);

	/* Return the price of a car at this location. */
	abstract public int queryCarsPrice(int tid, String location);


	// Room operations //

	/* Add rooms to a location.
	 * This should look a lot like addFlight, only keyed on a string location
	 * instead of a flight number.
	 */
	abstract public boolean addRooms(int tid, String location, int numRooms, int roomPrice);

	/* Delete all rooms from a location.
	 * It should not succeed if there are reservations for this location.
	 */
	abstract public boolean deleteRooms(int tid, String location);

	/* Return the number of rooms available at this location. */
	abstract public int queryRooms(int tid, String location);

	/* Return the price of a room at this location. */
	abstract public int queryRoomsPrice(int tid, String location);


	// Customer operations //

	/* Create a new customer and return their unique identifier. */
	abstract public int newCustomer(int tid);

	/* Create a new customer with the provided identifier. */
	abstract public boolean newCustomerId(int tid, int customerId);

	/* Remove this customer and all their associated reservations. */
	abstract public boolean deleteCustomer(int tid, int customerId);

	/*Return data structure containing customer reservation info */
	abstract public RMHashtable getCustomerReservations(int tid, int customerId);

	/* Return a bill. */
	abstract public String queryCustomerInfo(int tid, int customerId);

	/* Reserve a seat on this flight. */
	abstract public boolean reserveFlight(int tid, int customerId, int flightNumber);

	/* Reserve a car at this location. */
	abstract public boolean reserveCar(int tid, int customerId, String location, int numCars);

	/* Reserve a room at this location. */
	abstract public boolean reserveRoom(int tid, int customerId, String location, int numRooms);


	/* Reserve an itinerary. */
	abstract public boolean reserveItinerary(int tid, int customerId, Vector flightNumbers,
	                                         String location, int numCars, int numRooms);

	/* Start a new transaction and return its id */
	abstract public int start();

	/* Start a new transaction and return its id */
	abstract public boolean start(int transactionId);

	/* Attempt to commit the given transaction; return true upon success */
	abstract public boolean commit(int transactionId);

	/* Attempt to commit the given transaction with 2PC */
	abstract public boolean commit2PC(int transactionId);

	/* Prepare to commit */
	abstract public boolean commitRequest(int transactionId);

	/* Finish to commit */
	abstract public boolean commitFinish(int transactionId);

	/* Abort the given transaction */
	abstract public boolean abort(int transactionId);

	/* Shutdown the RM gracefully */
	abstract public boolean shutdown();

	/* If the RM is currently available for taking requests */
	abstract public boolean isAvailable();
}
