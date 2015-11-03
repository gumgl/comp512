// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package middleware;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.InitialContext;
import server.*;
import client.*;

@WebService(targetNamespace = "http://ns",
            endpointInterface = "server.ws.ResourceManager")
public class ResourceManagerImpl implements server.ws.ResourceManager {

    WSClient flightClient;
    WSClient carClient;
    WSClient roomClient;

    private final ConcurrentHashMap<Integer, Integer> customerIds =
            new ConcurrentHashMap<>();

    public ResourceManagerImpl() throws Exception {
        Context env = (Context) new InitialContext().lookup("java:comp/env");

        String flightServiceName = (String) env.lookup("flight-service-name");
        String flightServiceHost = (String) env.lookup("flight-service-host");
        Integer flightServicePort = (Integer) env.lookup("flight-service-port");
        flightClient = new WSClient(
                flightServiceName, flightServiceHost, flightServicePort);

        String carServiceName = (String) env.lookup("car-service-name");
        String carServiceHost = (String) env.lookup("car-service-host");
        Integer carServicePort = (Integer) env.lookup("car-service-port");
        carClient = new WSClient(
                carServiceName, carServiceHost, carServicePort);

        String roomServiceName = (String) env.lookup("room-service-name");
        String roomServiceHost = (String) env.lookup("room-service-host");
        Integer roomServicePort = (Integer) env.lookup("room-service-port");
        roomClient = new WSClient(
                roomServiceName, roomServiceHost, roomServicePort);
    }

    @Override
    public boolean addFlight(int id, int flightNumber,
                             int numSeats, int flightPrice) {
        Trace.info("MW::addFlight(" + id + ", " + flightNumber
                + ", " + numSeats + ", " + flightPrice + ")");
        try {
            return flightClient.proxy.
                    addFlight(id, flightNumber, numSeats, flightPrice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) {
        Trace.info("MW::deleteFlight(" + id + ", " + flightNumber + ")");
        try {
            return flightClient.proxy.deleteFlight(id, flightNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) {
        Trace.info("MW::queryFlight(" + id + ", " + flightNumber + ")");
        try {
            return flightClient.proxy.queryFlight(id, flightNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) {
        Trace.info("MW::queryFlightPrice(" + id + ", " + flightNumber + ")");
        try {
            return flightClient.proxy.queryFlightPrice(id, flightNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) {
        Trace.info("MW::addCars(" + id + ", " + location
                + ", " + numCars + ", " + carPrice + ")");
        try {
            return carClient.proxy.addCars(id, location, numCars, carPrice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) {
        Trace.info("MW::deleteCars(" + id + ", " + location + ")");
        try {
            return carClient.proxy.deleteCars(id, location);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int queryCars(int id, String location) {
        Trace.info("MW::queryCars(" + id + ", " + location + ")");
        try {
            return carClient.proxy.queryCars(id, location);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) {
        Trace.info("MW::queryCarsPrice(" + id + ", " + location + ")");
        try {
            return carClient.proxy.queryCarsPrice(id, location);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) {
        Trace.info("MW::addRooms(" + id + ", " + location
                + ", " + numRooms + ", " + roomPrice + ")");
        try {
            return roomClient.proxy.addRooms(id, location, numRooms, roomPrice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) {
        Trace.info("MW::deleteRooms(" + id + ", " + location + ")");
        try {
            return roomClient.proxy.deleteRooms(id, location);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int queryRooms(int id, String location) {
        Trace.info("MW::queryRooms(" + id + ", " + location + ")");
        try {
            return roomClient.proxy.queryRooms(id, location);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) {
        Trace.info("MW::queryRoomsPrice(" + id + ", " + location + ")");
        try {
            return roomClient.proxy.queryRoomsPrice(id, location);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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
            flightClient.proxy.newCustomerId(id, customerId);
            carClient.proxy.newCustomerId(id, customerId);
            roomClient.proxy.newCustomerId(id, customerId);
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
            flightClient.proxy.deleteCustomer(id, customerId);
            carClient.proxy.deleteCustomer(id, customerId);
            roomClient.proxy.deleteCustomer(id, customerId);
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
            s = s + flightClient.proxy.queryCustomerInfo(id, customerId) + "\n";
            s = s + carClient.proxy.queryCustomerInfo(id, customerId) + "\n";
            s = s + roomClient.proxy.queryCustomerInfo(id, customerId) + "\n";
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
        Trace.info("MW::reserveFlight(" + id + ", " + customerId +
                ", " + flightNumber + ")");
        try {
            return flightClient.proxy.reserveFlight(id, customerId, flightNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reserveCar(int id, int customerId, String location) {
        Trace.info("MW::reserveCar(" + id + ", " + customerId +
                ", " + location + ")");
        try {
            return carClient.proxy.reserveCar(id, customerId, location);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customerId, String location) {
        Trace.info("MW::reserveRoom(" + id + ", " + customerId +
                ", " + location + ")");
        try {
            return roomClient.proxy.reserveRoom(id, customerId, location);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reserveItinerary(int id, int customerId, Vector flightNumbers,
                                    String location, boolean car, boolean room) {
        Trace.info("MW::reserveItinerary(" + id + ", " + customerId + ", ...)");
        if (!customerIds.containsKey(customerId)) {
            Trace.info("MW::reserveItinerary(" + id + ", " + customerId +
                    ", ...) failed: customer doesn't exist.");
            return false;
        }
        boolean r = false;
        try {
            for (Object o : flightNumbers) {
                int flightNumber = Integer.parseInt((String) o);
                r &= reserveFlight(id, customerId, flightNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (car) {
            try {
                r &= reserveCar(id, customerId, location);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        if (room) {
            try {
                r &= reserveRoom(id, customerId, location);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return r;
    }

}
