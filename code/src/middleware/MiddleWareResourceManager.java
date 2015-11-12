package middleware;

import server.RMIResourceManager;
import server.Trace;
import system.ResourceManager;
import system.LocalResourceManager;
import transactions.InvalidTransactionIDException;
import transactions.TransactionManager;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

//TODO: Find a way to accumulate operations' RMIs until commit
public class MiddlewareResourceManager extends ResourceManager {

    RMIResourceManager flightRM;
    RMIResourceManager carRM;
    RMIResourceManager roomRM;
    LocalResourceManager customerRM = new LocalResourceManager();
    TransactionManager TM;
    /*private final ConcurrentHashMap<Integer, Integer> customerIds =
            new ConcurrentHashMap<Integer, Integer>();*/

    public MiddlewareResourceManager(RMIResourceManager flightRM,
                                     RMIResourceManager carRM,
                                     RMIResourceManager roomRM) {
        this.flightRM = flightRM;
        this.carRM = carRM;
        this.roomRM = roomRM;

        this.TM = new TransactionManager(); // We need it for
    }

    /* Same logic for all operations.
     * Returns the correct RM on which the operation should be executed */
    private ResourceManager handleOperation(int tid, ResourceManager relevantRM) throws InvalidTransactionIDException {
        if (!TM.isTransactionIdValid(tid)) {
            String message = String.format("Transaction #%d is not valid\n",tid);
            Trace.error(message);
            throw new InvalidTransactionIDException(message);
        } else {
            TM.enlist(tid, relevantRM);
            return relevantRM;
        }
    }

    @Override
    public boolean addFlight(int tid, int flightNumber, int numSeats, int flightPrice) throws Exception {
        return handleOperation(tid, flightRM).addFlight(tid, flightNumber, numSeats, flightPrice);
    }

    @Override
    public boolean deleteFlight(int tid, int flightNumber) throws Exception {
        return handleOperation(tid, flightRM).deleteFlight(tid, flightNumber);
    }

    @Override
    public int queryFlight(int tid, int flightNumber) throws Exception {
        return handleOperation(tid, flightRM).queryFlight(tid, flightNumber);
    }

    @Override
    public int queryFlightPrice(int tid, int flightNumber) throws Exception {
        return handleOperation(tid, flightRM).queryFlightPrice(tid, flightNumber);
    }

    @Override
    public boolean addCars(int tid, String location, int numCars, int carPrice) throws Exception {
        return handleOperation(tid, carRM).addCars(tid, location, numCars, carPrice);
    }

    @Override
    public boolean deleteCars(int tid, String location) throws Exception {
        return handleOperation(tid, carRM).deleteCars(tid, location);
    }

    @Override
    public int queryCars(int tid, String location) throws Exception {
        handleOperation(tid, carRM);
        return handleOperation(tid, carRM).queryCars(tid, location);
    }

    @Override
    public int queryCarsPrice(int tid, String location) throws Exception {
        return handleOperation(tid, carRM).queryCarsPrice(tid, location);
    }

    @Override
    public boolean addRooms(int tid, String location, int numRooms, int roomPrice) throws Exception {
        return handleOperation(tid, roomRM).addRooms(tid, location, numRooms, roomPrice);
    }

    @Override
    public boolean deleteRooms(int tid, String location) throws Exception {
        return handleOperation(tid, roomRM).deleteRooms(tid, location);
    }

    @Override
    public int queryRooms(int tid, String location) throws Exception {
        return handleOperation(tid, roomRM).queryRooms(tid, location);
    }

    @Override
    public int queryRoomsPrice(int tid, String location) throws Exception {
        return handleOperation(tid, roomRM).queryRoomsPrice(tid, location);
    }

    @Override
    public int newCustomer(int tid) throws Exception {
        int customerId  = handleOperation(tid, customerRM).newCustomer(tid);
        handleOperation(tid, flightRM).newCustomerId(tid, customerId);
        handleOperation(tid, carRM).newCustomerId(tid, customerId);
        handleOperation(tid, roomRM).newCustomerId(tid, customerId);
        return customerId;
    }

    @Override
    public boolean newCustomerId(int tid, int customerId) throws Exception {
        boolean success = true;
        success &= handleOperation(tid, customerRM).newCustomerId(tid, customerId);
        success &= handleOperation(tid, flightRM).newCustomerId(tid, customerId);
        success &= handleOperation(tid, carRM).newCustomerId(tid, customerId);
        success &= handleOperation(tid, roomRM).newCustomerId(tid, customerId);
        return success;
    }

    @Override
    public boolean deleteCustomer(int tid, int customerId) throws Exception {
        boolean success = true;
        success &= handleOperation(tid, customerRM).deleteCustomer(tid, customerId);
        success &= handleOperation(tid, flightRM).deleteCustomer(tid, customerId);
        success &= handleOperation(tid, carRM).deleteCustomer(tid, customerId);
        success &= handleOperation(tid, roomRM).deleteCustomer(tid, customerId);
        return success;
    }

    @Override
    public String queryCustomerInfo(int tid, int customerId) throws Exception {
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
    public boolean reserveFlight(int tid, int customerId, int flightNumber) throws Exception {
        return handleOperation(tid, flightRM).reserveFlight(tid, customerId, flightNumber);
    }

    @Override
    public boolean reserveCar(int tid, int customerId, String location) throws Exception {
        return handleOperation(tid, carRM).reserveCar(tid, customerId, location);
    }

    @Override
    public boolean reserveRoom(int tid, int customerId, String location) throws Exception {
        return handleOperation(tid, roomRM).reserveRoom(tid, customerId, location);
    }

    @Override
    public boolean reserveItinerary(int tid, int customerId, Vector flightNumbers, String location, boolean car, boolean room) throws Exception {
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
        if (car) {
            try {
                r &= reserveCar(tid, customerId, location);
                Trace.info("MW: Reserve car " + (r ? "OK" : "Failed"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        if (room) {
            try {
                r &= reserveRoom(tid, customerId, location);
                Trace.info("MW: Reserve room " + (r ? "OK" : "Failed"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return r;
    }

    @Override
    public int start() throws Exception {
        Trace.info("MW::start()");
        int id = TM.start(); // First locally start with latest TC
        flightRM.start(id); // Then forward start with same TC
        carRM.start(id);
        roomRM.start(id);
        return id;
    }

    @Override
    public boolean start(int transactionId) throws Exception {
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
    public boolean commit(int transactionId) throws Exception {
        Trace.info(String.format("MW::commit(%d)", transactionId));
        return TM.commit(transactionId); // The TM also takes care of calling t.enlistedTMs.commit()
    }

    @Override
    public boolean abort(int transactionId) throws Exception {
        Trace.info(String.format("MW::abort(%d)", transactionId));
        return TM.abort(transactionId);
    }

    @Override
    public boolean shutdown() {
        boolean success = true;
        success &= flightRM.shutdown();
        success &= carRM.shutdown();
        success &= roomRM.shutdown();
        active = !success;
        return success;
    }
}
