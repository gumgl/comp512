// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package system;


public class Car extends ReservableItem {

    public Car() {}

    public Car(String location, int numCars, int carPrice) {
        super(location, numCars, carPrice);
    }

    public String getKey() {
        return Car.getKey(getLocation());
    }

    public static String getKey(String location) {
        String s = "car-" + location;
        return s.toLowerCase();
    }

    @Override
    public Car copy() {
        Car cpy = new Car();
        cpy.copyInternals(this);
        return cpy;
    }
}

