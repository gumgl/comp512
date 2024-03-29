// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package system;


public class Flight extends ReservableItem {

    public Flight() {}
    public Flight(int flightNumber, int numSeats, int flightPrice) {
        super(Integer.toString(flightNumber), numSeats, flightPrice);
    }

    public String getKey() {
        return Flight.getKey(Integer.parseInt(getLocation()));
    }

    public static String getKey(int flightNumber) {
        String s = "flight-" + flightNumber;
        return s.toLowerCase();
    }

    @Override
    public Flight copy() {
        Flight cpy = new Flight();
        cpy.copyInternals(this);
        return cpy;
    }
}
