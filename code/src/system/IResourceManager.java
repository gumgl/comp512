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
public interface IResourceManager {
    
    // Flight operations //
    
    /* Add seats to a flight.  
     * In general, this will be used to create a new flight, but it should be 
     * possible to add seats to an existing flight.  Adding to an existing 
     * flight should overwrite the current price of the available seats.
     *
     * @return success.
     */
    public boolean addFlight(int tid, int flightNumber, int numSeats, int flightPrice) throws Exception;

    /**
     * Delete the entire flight.
     * This implies deletion of this flight and all its seats.  If there is a
     * reservation on the flight, then the flight cannot be deleted.
     *
     * @return success.
     */
    public boolean deleteFlight(int tid, int flightNumber) throws Exception;

    /* Return the number of empty seats in this flight. */
    public int queryFlight(int tid, int flightNumber) throws Exception;

    /* Return the price of a seat on this flight. */
    public int queryFlightPrice(int tid, int flightNumber) throws Exception;


    // Car operations //

    /* Add cars to a location.
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     */
    public boolean addCars(int tid, String location, int numCars, int carPrice) throws Exception;

    /* Delete all cars from a location.
     * It should not succeed if there are reservations for this location.
     */
    public boolean deleteCars(int tid, String location) throws Exception;

    /* Return the number of cars available at this location. */
    public int queryCars(int tid, String location) throws Exception;

    /* Return the price of a car at this location. */
    public int queryCarsPrice(int tid, String location) throws Exception;


    // Room operations //

    /* Add rooms to a location.
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     */
    public boolean addRooms(int tid, String location, int numRooms, int roomPrice) throws Exception;

    /* Delete all rooms from a location.
     * It should not succeed if there are reservations for this location.
     */
    public boolean deleteRooms(int tid, String location) throws Exception;

    /* Return the number of rooms available at this location. */
    public int queryRooms(int tid, String location) throws Exception;

    /* Return the price of a room at this location. */
    public int queryRoomsPrice(int tid, String location) throws Exception;


    // Customer operations //

    /* Create a new customer and return their unique identifier. */
    public int newCustomer(int tid) throws Exception;

    /* Create a new customer with the provided identifier. */
    public boolean newCustomerId(int tid, int customerId) throws Exception;

    /* Remove this customer and all their associated reservations. */
    public boolean deleteCustomer(int tid, int customerId) throws Exception;

    /* Return a bill. */
    public String queryCustomerInfo(int tid, int customerId) throws Exception;

    /* Reserve a seat on this flight. */
    public boolean reserveFlight(int tid, int customerId, int flightNumber) throws Exception;

    /* Reserve a car at this location. */
    public boolean reserveCar(int tid, int customerId, String location) throws Exception;

    /* Reserve a room at this location. */
    public boolean reserveRoom(int tid, int customerId, String location) throws Exception;


    /* Reserve an itinerary. */
    public boolean reserveItinerary(int tid, int customerId, Vector flightNumbers,
                                    String location, boolean car, boolean room) throws Exception;

    /* Start a new transaction and return its id */
    public int start() throws Exception;

    /* Start a new transaction and return its id */
    public boolean start(int transactionId) throws Exception;

    /* Attempt to commit the given transaction; return true upon success */
    public boolean commit(int transactionId) throws Exception;

    /* Abort the given transaction */
    public boolean abort(int transactionId) throws Exception;
}
