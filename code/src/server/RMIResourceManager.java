package server;

// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

import rmi.Invocation;
import rmi.Invokable;
import system.IResourceManager;

import java.util.Vector;

/*
 * This class will forward all RM calls to an RMI.Invokable and return the result
 */

public class RMIResourceManager implements IResourceManager {

    protected Invokable target;

    public RMIResourceManager(Invokable target){
        this.target = target;
    }

    // Flight operations //

    @Override
    public boolean addFlight(int id, int flightNumber,
                             int numSeats, int flightPrice) throws Exception {
        try {
            Invocation invocation = new Invocation("addFlight");
            invocation.addParam(id);
            invocation.addParam(flightNumber);
            invocation.addParam(numSeats);
            invocation.addParam(flightPrice);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) throws Exception {
        try {
            Invocation invocation = new Invocation("deleteFlight");
            invocation.addParam(id);
            invocation.addParam(flightNumber);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws Exception {
        try {
            Invocation invocation = new Invocation("queryFlight");
            invocation.addParam(id);
            invocation.addParam(flightNumber);
            return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws Exception {
        try {
            Invocation invocation = new Invocation("queryFlightPrice");
            invocation.addParam(id);
            invocation.addParam(flightNumber);
        return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Car operations //

    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) throws Exception {
        try {
            Invocation invocation = new Invocation("addCars");
            invocation.addParam(id);
            invocation.addParam(location);
            invocation.addParam(numCars);
            invocation.addParam(carPrice);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("deleteCars");
            invocation.addParam(id);
            invocation.addParam(location);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int queryCars(int id, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("queryCars");
            invocation.addParam(id);
            invocation.addParam(location);
            return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("queryCarsPrice");
            invocation.addParam(id);
            invocation.addParam(location);
            return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Room operations //

    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) throws Exception {
        try {
            Invocation invocation = new Invocation("addRooms");
            invocation.addParam(id);
            invocation.addParam(location);
            invocation.addParam(numRooms);
            invocation.addParam(roomPrice);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("deleteRooms");
            invocation.addParam(id);
            invocation.addParam(location);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("queryRooms");
            invocation.addParam(id);
            invocation.addParam(location);
            return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("queryRoomsPrice");
            invocation.addParam(id);
            invocation.addParam(location);
            return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // Customer operations //

    @Override
    public int newCustomer(int id) throws Exception {
        try {
            Invocation invocation = new Invocation("newCustomer");
            invocation.addParam(id);
            return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean newCustomerId(int id, int customerId) throws Exception {
        try {
            Invocation invocation = new Invocation("newCustomerId");
            invocation.addParam(id);
            invocation.addParam(customerId);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customerId) throws Exception {
        try {
            Invocation invocation = new Invocation("deleteCustomer");
            invocation.addParam(id);
            invocation.addParam(customerId);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customerId) throws Exception {
        try {
            Invocation invocation = new Invocation("queryCustomerInfo");
            invocation.addParam(id);
            invocation.addParam(customerId);
            return target.invoke(invocation).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean reserveFlight(int id, int customerId, int flightNumber) throws Exception {
        try {
            Invocation invocation = new Invocation("reserveFlight");
            invocation.addParam(id);
            invocation.addParam(customerId);
            invocation.addParam(flightNumber);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reserveCar(int id, int customerId, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("reserveCar");
            invocation.addParam(id);
            invocation.addParam(customerId);
            invocation.addParam(location);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customerId, String location) throws Exception {
        try {
            Invocation invocation = new Invocation("reserveRoom");
            invocation.addParam(id);
            invocation.addParam(customerId);
            invocation.addParam(location);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean reserveItinerary(int id, int customerId, Vector flightNumbers,
                                    String location, boolean car, boolean room) throws Exception {
        try {
            Invocation invocation = new Invocation("reserveItinerary");
            invocation.addParam(id);
            invocation.addParam(customerId);
            invocation.addParam(flightNumbers);
            invocation.addParam(location);
            invocation.addParam(car);
            invocation.addParam(room);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int start() throws Exception {
        try {
            Invocation invocation = new Invocation("start");
            return (Integer) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean commit(int transactionId) throws Exception {
        try {
            Invocation invocation = new Invocation("commit");
            invocation.addParam(transactionId);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean abort(int transactionId) throws Exception {
        try {
            Invocation invocation = new Invocation("abort");
            invocation.addParam(transactionId);
            return (Boolean) target.invoke(invocation);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
