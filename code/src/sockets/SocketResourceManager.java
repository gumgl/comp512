package sockets;

// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

import system.ResourceManager;

import java.net.InetAddress;
import java.util.Vector;

/*
 * This class will forward all RM calls to a socket connection and return the result
 */

public class SocketResourceManager implements ResourceManager {

    protected RMISender sender;

    public SocketResourceManager(InetAddress host, int port) {
        sender = new RMISender(host, port);
    }

    // Flight operations //

    // Create a new flight, or add seats to existing flight.
    // Note: if flightPrice <= 0 and the flight already exists, it maintains
    // its current price.
    @Override
    public boolean addFlight(int id, int flightNumber,
                             int numSeats, int flightPrice) {
        try {
            RMI rmi = new RMI("addFlight");
            rmi.addParam(id);
            rmi.addParam(flightNumber);
            rmi.addParam(numSeats);
            rmi.addParam(flightPrice);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) {
        try {
            RMI rmi = new RMI("deleteFlight");
            rmi.addParam(id);
            rmi.addParam(flightNumber);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Returns the number of empty seats on this flight.
    @Override
    public int queryFlight(int id, int flightNumber) {
        try {
            RMI rmi = new RMI("queryFlight");
            rmi.addParam(id);
            rmi.addParam(flightNumber);
            return (Integer) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Returns price of this flight.
    public int queryFlightPrice(int id, int flightNumber) {
        try {
            RMI rmi = new RMI("queryFlightPrice");
            rmi.addParam(id);
            rmi.addParam(flightNumber);
        return (Integer) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
    // Returns the number of reservations for this flight.
    public int queryFlightReservations(int id, int flightNumber) {
        Trace.info("RM::queryFlightReservations(" + id
                + ", #" + flightNumber + ") called.");
        RMInteger numReservations = (RMInteger) readData(id,
                Flight.getNumReservationsKey(flightNumber));
        if (numReservations == null) {
            numReservations = new RMInteger(0);
       }
        Trace.info("RM::queryFlightReservations(" + id +
                ", #" + flightNumber + ") = " + numReservations);
        return numReservations.getValue();
    }
    */

    /*
    // Frees flight reservation record. Flight reservation records help us
    // make sure we don't delete a flight if one or more customers are
    // holding reservations.
    public boolean freeFlightReservation(int id, int flightNumber) {
        Trace.info("RM::freeFlightReservations(" + id + ", "
                + flightNumber + ") called.");
        RMInteger numReservations = (RMInteger) readData(id,
                Flight.getNumReservationsKey(flightNumber));
        if (numReservations != null) {
            numReservations = new RMInteger(
                    Math.max(0, numReservations.getValue() - 1));
        }
        writeData(id, Flight.getNumReservationsKey(flightNumber), numReservations);
        Trace.info("RM::freeFlightReservations(" + id + ", "
                + flightNumber + ") OK: reservations = " + numReservations);
        return true;
    }
    */


    // Car operations //

    // Create a new car location or add cars to an existing location.
    // Note: if price <= 0 and the car location already exists, it maintains
    // its current price.
    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) {
        try {
            RMI rmi = new RMI("addCars");
            rmi.addParam(id);
            rmi.addParam(location);
            rmi.addParam(numCars);
            rmi.addParam(carPrice);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete cars from a location.
    @Override
    public boolean deleteCars(int id, String location) {
        try {
            RMI rmi = new RMI("deleteCars");
            rmi.addParam(id);
            rmi.addParam(location);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Returns the number of cars available at a location.
    @Override
    public int queryCars(int id, String location) {
        try {
            RMI rmi = new RMI("queryCars");
            rmi.addParam(id);
            rmi.addParam(location);
            return (Integer) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Returns price of cars at this location.
    @Override
    public int queryCarsPrice(int id, String location) {
        try {
            RMI rmi = new RMI("queryCarsPrice");
            rmi.addParam(id);
            rmi.addParam(location);
            return (Integer) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // Room operations //

    // Create a new room location or add rooms to an existing location.
    // Note: if price <= 0 and the room location already exists, it maintains
    // its current price.
    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) {
        try {
            RMI rmi = new RMI("addRooms");
            rmi.addParam(id);
            rmi.addParam(location);
            rmi.addParam(numRooms);
            rmi.addParam(roomPrice);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete rooms from a location.
    @Override
    public boolean deleteRooms(int id, String location) {
        try {
            RMI rmi = new RMI("deleteRooms");
            rmi.addParam(id);
            rmi.addParam(location);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Returns the number of rooms available at a location.
    @Override
    public int queryRooms(int id, String location) {
        try {
            RMI rmi = new RMI("queryRooms");
            rmi.addParam(id);
            rmi.addParam(location);
            return (Integer) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Returns room price at this location.
    @Override
    public int queryRoomsPrice(int id, String location) {
        try {
            RMI rmi = new RMI("queryRoomsPrice");
            rmi.addParam(id);
            rmi.addParam(location);
            return (Integer) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // Customer operations //

    @Override
    public int newCustomer(int id) {
        try {
            RMI rmi = new RMI("newCustomer");
            rmi.addParam(id);
            return (Integer) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // This method makes testing easier.
    @Override
    public boolean newCustomerId(int id, int customerId) {
        try {
            RMI rmi = new RMI("newCustomerId");
            rmi.addParam(id);
            rmi.addParam(customerId);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete customer from the database.
    @Override
    public boolean deleteCustomer(int id, int customerId) {
        try {
            RMI rmi = new RMI("deleteCustomer");
            rmi.addParam(id);
            rmi.addParam(customerId);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Return a bill.
    @Override
    public String queryCustomerInfo(int id, int customerId) {
        try {
            RMI rmi = new RMI("queryCustomerInfo");
            rmi.addParam(id);
            rmi.addParam(customerId);
            return sender.invoke(rmi).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Add flight reservation to this customer.
    @Override
    public boolean reserveFlight(int id, int customerId, int flightNumber) {
        try {
            RMI rmi = new RMI("reserveFlight");
            rmi.addParam(id);
            rmi.addParam(customerId);
            rmi.addParam(flightNumber);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add car reservation to this customer.
    @Override
    public boolean reserveCar(int id, int customerId, String location) {
        try {
            RMI rmi = new RMI("reserveCar");
            rmi.addParam(id);
            rmi.addParam(customerId);
            rmi.addParam(location);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add room reservation to this customer.
    @Override
    public boolean reserveRoom(int id, int customerId, String location) {
        try {
            RMI rmi = new RMI("reserveRoom");
            rmi.addParam(id);
            rmi.addParam(customerId);
            rmi.addParam(location);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Reserve an itinerary.
    @Override
    public boolean reserveItinerary(int id, int customerId, Vector flightNumbers,
                                    String location, boolean car, boolean room) {
        try {
            RMI rmi = new RMI("reserveItinerary");
            rmi.addParam(id);
            rmi.addParam(customerId);
            rmi.addParam(flightNumbers);
            rmi.addParam(location);
            rmi.addParam(car);
            rmi.addParam(room);
            return (Boolean) sender.invoke(rmi);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
