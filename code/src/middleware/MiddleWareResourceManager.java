package middleware;

import server.Trace;
import system.ResourceManager;
import transactions.TransactionManager;

import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

//TODO: Find a way to accumulate operations' RMIs until commit
public class MiddleWareResourceManager implements ResourceManager {

    ResourceManager flightRM;
    ResourceManager carRM;
    ResourceManager roomRM;
    TransactionManager TM;
    private final ConcurrentHashMap<Integer, Integer> customerIds =
            new ConcurrentHashMap<Integer, Integer>();

    public MiddleWareResourceManager(ResourceManager flightRM,
                                     ResourceManager carRM,
                                     ResourceManager roomRM) {
        this.flightRM = flightRM;
        this.carRM = carRM;
        this.roomRM = roomRM;
        TM = new TransactionManager();
    }

    @Override
    public boolean addFlight(int id, int flightNumber, int numSeats, int flightPrice) {
        TM.enlist(id, flightRM);
        return flightRM.addFlight(id, flightNumber, numSeats, flightPrice);
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) {
        TM.enlist(id, flightRM);
        return flightRM.deleteFlight(id, flightNumber);
    }

    @Override
    public int queryFlight(int id, int flightNumber) {
        TM.enlist(id, flightRM);
        return flightRM.queryFlight(id, flightNumber);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) {
        TM.enlist(id, flightRM);
        return flightRM.queryFlightPrice(id, flightNumber);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) {
        TM.enlist(id, carRM);
        return carRM.addCars(id, location, numCars, carPrice);
    }

    @Override
    public boolean deleteCars(int id, String location) {
        TM.enlist(id, carRM);
        return carRM.deleteCars(id, location);
    }

    @Override
    public int queryCars(int id, String location) {
        TM.enlist(id, carRM);
        return carRM.queryCars(id, location);
    }

    @Override
    public int queryCarsPrice(int id, String location) {
        TM.enlist(id, carRM);
        return carRM.queryCarsPrice(id, location);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) {
        TM.enlist(id, roomRM);
        return roomRM.addRooms(id, location, numRooms, roomPrice);
    }

    @Override
    public boolean deleteRooms(int id, String location) {
        TM.enlist(id, roomRM);
        return roomRM.deleteRooms(id, location);
    }

    @Override
    public int queryRooms(int id, String location) {
        TM.enlist(id, roomRM);
        return roomRM.queryRooms(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) {
        TM.enlist(id, roomRM);
        return roomRM.queryRoomsPrice(id, location);
    }

    @Override
    public int newCustomer(int id) {
        TM.enlist(id, this); // TODO: This solution is probably wrong but this case needs to be handled
        Trace.info("MW::newCustomer(" + id + ")");
        int customerId = Integer.parseInt(String.valueOf(id) +
                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                String.valueOf(Math.round(Math.random() * 100 + 1)));
        if (newCustomerId(id, customerId)) {
            return customerId;
        } else {
            return -1;
        }
    }

    @Override
    public boolean newCustomerId(int id, int customerId) {
        TM.enlist(id, this); // TODO: This solution is probably wrong but this case needs to be handled
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
    }

    @Override
    public boolean deleteCustomer(int id, int customerId) {
        TM.enlist(id, this); // TODO: This solution is probably wrong but this case needs to be handled
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
    }

    @Override
    public String queryCustomerInfo(int id, int customerId) {
        TM.enlist(id, this); // TODO: This solution is probably wrong but this case needs to be handled
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
    }

    @Override
    public boolean reserveFlight(int id, int customerId, int flightNumber) {
        TM.enlist(id, flightRM);
        return flightRM.reserveFlight(id, customerId, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customerId, String location) {
        TM.enlist(id, carRM);
        return carRM.reserveCar(id, customerId, location);
    }

    @Override
    public boolean reserveRoom(int id, int customerId, String location) {
        TM.enlist(id, roomRM);
        return roomRM.reserveRoom(id, customerId, location);
    }

    @Override
    public boolean reserveItinerary(int id, int customerId, Vector flightNumbers, String location, boolean car, boolean room) {
        Trace.info("MW::reserveItinerary(" + id + ", " + customerId + ", ...)");
        if (!customerIds.containsKey(customerId)) {
            Trace.info("MW::reserveItinerary(" + id + ", " + customerId +
                    ", ...) failed: customer doesn't exist.");
            return false;
        }
        boolean r = true;
        try {
            for (Object o : flightNumbers) {
                TM.enlist(id, flightRM); // TODO: Perhaps only enlist if operation is successful
                int flightNumber = Integer.parseInt((String) o);
                r &= reserveFlight(id, customerId, flightNumber);
                Trace.info("MW: Reserve flight "+ (r?"OK":"Failed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (car) {
            try {
                TM.enlist(id, carRM);
                r &= reserveCar(id, customerId, location);
                Trace.info("MW: Reserve car "+ (r?"OK":"Failed"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        if (room) {
            try {
                TM.enlist(id, roomRM);
                r &= reserveRoom(id, customerId, location);
                Trace.info("MW: Reserve room "+ (r?"OK":"Failed"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return r;
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
