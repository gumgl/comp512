package server;

import server.ws.ResourceManager;

import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Guillaume on 2015-11-10.
 */
public class MiddleWareResourceManager implements ResourceManager {

    ResourceManager flightRM;
    ResourceManager carRM;
    ResourceManager roomRM;
    private final ConcurrentHashMap<Integer, Integer> customerIds =
            new ConcurrentHashMap<Integer, Integer>();

    public MiddleWareResourceManager(ResourceManager flightRM,
                                     ResourceManager carRM,
                                     ResourceManager roomRM) {
        this.flightRM = flightRM;
        this.carRM = carRM;
        this.roomRM = roomRM;
    }

    @Override
    public boolean addFlight(int id, int flightNumber, int numSeats, int flightPrice) {
        return flightRM.addFlight(id, flightNumber, numSeats, flightPrice);
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) {
        return flightRM.deleteFlight(id, flightNumber);
    }

    @Override
    public int queryFlight(int id, int flightNumber) {
        return flightRM.queryFlight(id, flightNumber);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) {
        return flightRM.queryFlightPrice(id, flightNumber);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) {
        return carRM.addCars(id, location, numCars, carPrice);
    }

    @Override
    public boolean deleteCars(int id, String location) {
        return carRM.deleteCars(id, location);
    }

    @Override
    public int queryCars(int id, String location) {
        return carRM.queryCars(id, location);
    }

    @Override
    public int queryCarsPrice(int id, String location) {
        return carRM.queryCarsPrice(id, location);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) {
        return roomRM.addRooms(id, location, numRooms, roomPrice);
    }

    @Override
    public boolean deleteRooms(int id, String location) {
        return roomRM.deleteRooms(id, location);
    }

    @Override
    public int queryRooms(int id, String location) {
        return roomRM.queryRooms(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) {
        return roomRM.queryRoomsPrice(id, location);
    }

    @Override
    public int newCustomer(int id) {
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
        return flightRM.reserveFlight(id, customerId, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customerId, String location) {
        return carRM.reserveCar(id, customerId, location);
    }

    @Override
    public boolean reserveRoom(int id, int customerId, String location) {
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
                r &= reserveCar(id, customerId, location);
                Trace.info("MW: Reserve car "+ (r?"OK":"Failed"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        if (room) {
            try {
                r &= reserveRoom(id, customerId, location);
                Trace.info("MW: Reserve room "+ (r?"OK":"Failed"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return r;
    }
}
