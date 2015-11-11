package middleware;

import server.Trace;
import system.IResourceManager;
import transactions.Transaction;
import transactions.TransactionManager;

import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

//TODO: Find a way to accumulate operations' RMIs until commit
public class MiddleWareResourceManager implements IResourceManager {

    IResourceManager flightRM;
    IResourceManager carRM;
    IResourceManager roomRM;
    TransactionManager TM;
    private final ConcurrentHashMap<Integer, Integer> customerIds =
            new ConcurrentHashMap<Integer, Integer>();

    public MiddleWareResourceManager(IResourceManager flightRM,
                                     IResourceManager carRM,
                                     IResourceManager roomRM) {
        this.flightRM = flightRM;
        this.carRM = carRM;
        this.roomRM = roomRM;

        this.TM = new TransactionManager(this);
    }

    /* Same logic for all operations.
     * Returns the correct RM on which the operation should be executed
     * (either transaction buffer or remote RM) */
    private IResourceManager handleOperation(int id, IResourceManager relevantRM) {
        if (id == -1) { // Special case, we process this directly without transaction buffering
            return relevantRM;
        } else if (!TM.transactionExists(id)) {
            Trace.error(String.format("Transaction #%d does not exist",id));
            return null; // TODO: This will cause NullPointerExceptions every time an transactionId is not valid!
        } else {
            Transaction t = TM.getTransaction(id);
            //t.enlist(relevantRM);
            return t.rm;
        }
    }

    @Override
    public boolean addFlight(int id, int flightNumber, int numSeats, int flightPrice) {
        return handleOperation(id, flightRM).addFlight(id, flightNumber, numSeats, flightPrice);
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) {
        return handleOperation(id, flightRM).deleteFlight(id, flightNumber);
    }

    @Override
    public int queryFlight(int id, int flightNumber) {
        return handleOperation(id, flightRM).queryFlight(id, flightNumber);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) {
        return handleOperation(id, flightRM).queryFlightPrice(id, flightNumber);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) {
        return handleOperation(id, carRM).addCars(id, location, numCars, carPrice);
    }

    @Override
    public boolean deleteCars(int id, String location) {
        return handleOperation(id, carRM).deleteCars(id, location);
    }

    @Override
    public int queryCars(int id, String location) {
        handleOperation(id, carRM);
        return handleOperation(id, carRM).queryCars(id, location);
    }

    @Override
    public int queryCarsPrice(int id, String location) {
        return handleOperation(id, carRM).queryCarsPrice(id, location);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) {
        return handleOperation(id, roomRM).addRooms(id, location, numRooms, roomPrice);
    }

    @Override
    public boolean deleteRooms(int id, String location) {
        return handleOperation(id, roomRM).deleteRooms(id, location);
    }

    @Override
    public int queryRooms(int id, String location) {
        return handleOperation(id, roomRM).queryRooms(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) {
        return handleOperation(id, roomRM).queryRoomsPrice(id, location);
    }

    @Override
    public int newCustomer(int id) {
        IResourceManager whoToCall = handleOperation(id, this);
        if (whoToCall == this) { // Don't actually call (infinite recursion) just do the work straight up
            Trace.info("MW::newCustomer(" + id + ")");
            int customerId = Integer.parseInt(String.valueOf(id) +
                    String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                    String.valueOf(Math.round(Math.random() * 100 + 1)));
            if (newCustomerId(id, customerId)) {
                return customerId;
            } else {
                return -1;
            }
        } else
            return whoToCall.newCustomer(id);
    }

    @Override
    public boolean newCustomerId(int id, int customerId) {
        IResourceManager whoToCall = handleOperation(id, this);
        if (whoToCall == this) {
            Trace.info("MW::newCustomer(" + id + ", " + customerId + ")");
            synchronized (customerIds) {
                if (customerIds.containsKey(customerId)) {
                    Trace.info("MW::newCustomer(" + id + ", " + customerId +
                            ") failed: customer already exists.");
                    return false;
                }
                customerIds.put(customerId, 0);
            }
            try {
                flightRM.newCustomerId(id, customerId);
                carRM.newCustomerId(id, customerId);
                roomRM.newCustomerId(id, customerId);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else
            return whoToCall.newCustomerId(id, customerId);
    }

    @Override
    public boolean deleteCustomer(int id, int customerId) {
        IResourceManager whoToCall = handleOperation(id, this);
        if (whoToCall == this) {
            Trace.info("MW::deleteCustomer(" + id + ", " + customerId + ")");
            synchronized (customerIds) {
                if (!customerIds.containsKey(customerId)) {
                    Trace.info("MW::deleteCustomer(" + id + ", " + customerId +
                            ") failed: customer doesn't exist.");
                    return false;
                }
                customerIds.remove(customerId);
            }
            try {
                flightRM.deleteCustomer(id, customerId);
                carRM.deleteCustomer(id, customerId);
                roomRM.deleteCustomer(id, customerId);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else
            return whoToCall.deleteCustomer(id, customerId);
    }

    @Override
    public String queryCustomerInfo(int id, int customerId) {
        IResourceManager whoToCall = handleOperation(id, this);
        if (whoToCall == this) {
            Trace.info("MW::queryCustomerInfo(" + id + ", " + customerId + ")");
            String s = "";
            if (!customerIds.containsKey(customerId)) {
                Trace.info("MW::deleteCustomer(" + id + ", " + customerId +
                        ") failed: customer doesn't exist.");
                return "";
            }
            try {
                s = s + flightRM.queryCustomerInfo(id, customerId) + "\n";
                s = s + carRM.queryCustomerInfo(id, customerId) + "\n";
                s = s + roomRM.queryCustomerInfo(id, customerId) + "\n";
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
            Trace.info("MW::queryCustomerInfo(" + id + ", " + customerId + "): \n");
            System.out.println(s);
            return s;
        } else
            return whoToCall.queryCustomerInfo(id, customerId);
    }

    @Override
    public boolean reserveFlight(int id, int customerId, int flightNumber) {
        return handleOperation(id, flightRM).reserveFlight(id, customerId, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customerId, String location) {
        return handleOperation(id, carRM).reserveCar(id, customerId, location);
    }

    @Override
    public boolean reserveRoom(int id, int customerId, String location) {
        return handleOperation(id, roomRM).reserveRoom(id, customerId, location);
    }

    @Override
    public boolean reserveItinerary(int id, int customerId, Vector flightNumbers, String location, boolean car, boolean room) {
        IResourceManager whoToCall = handleOperation(id, this);
        if (whoToCall == this) {
            Trace.info("MW::reserveItinerary(" + id + ", " + customerId + ", ...)");
            if (!customerIds.containsKey(customerId)) {
                Trace.info("MW::reserveItinerary(" + id + ", " + customerId +
                        ", ...) failed: customer doesn't exist.");
                return false;
            }
            boolean r = true;
            try {
                for (Object o : flightNumbers) {
                    handleOperation(id, flightRM); // TODO: Perhaps only enlist if operation is successful
                    int flightNumber = Integer.parseInt((String) o);
                    r &= reserveFlight(id, customerId, flightNumber);
                    Trace.info("MW: Reserve flight " + (r ? "OK" : "Failed"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if (car) {
                try {
                    handleOperation(id, carRM);
                    r &= reserveCar(id, customerId, location);
                    Trace.info("MW: Reserve car " + (r ? "OK" : "Failed"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if (room) {
                try {
                    handleOperation(id, roomRM);
                    r &= reserveRoom(id, customerId, location);
                    Trace.info("MW: Reserve room " + (r ? "OK" : "Failed"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return r;
        } else
            return whoToCall.reserveItinerary(id, customerId, flightNumbers, location, car, room);
    }

    @Override
    public int start() {
        return TM.start();
    }

    @Override
    public boolean commit(int transactionId) {
        return TM.commit(transactionId);
    }

    @Override
    public boolean abort(int transactionId) {
        return TM.abort(transactionId);
    }
}
