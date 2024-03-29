// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package system;


public class Room extends ReservableItem {

	public Room() {}
	public Room(String location, int numRooms, int roomPrice) {
		super(location, numRooms, roomPrice);
	}

	public String getKey() {
		return Room.getKey(getLocation());
	}

	public static String getKey(String location) {
		String s = "room-" + location;
		return s.toLowerCase();
	}

	public Room copy() {
		Room cpy = new Room();
		cpy.copyInternals(this);
		return cpy;
	}
}

