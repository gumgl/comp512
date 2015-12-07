// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package system;

import helper.SerializerHelper;
import server.Trace;
import transactions.Transaction;
import transactions.TransactionManager;

import java.util.*;

public class LocalResourceManager extends ResourceManager {
	public boolean active = true;
	public final String storagePath;
	public final ResourceManager.Type type;

	/* One transaction manager shared among threads */
	public final TransactionManager TM;

	public LocalResourceManager(ResourceManager.Type type) {
		this.type = type;
		this.storagePath = "./persistent/" + type.name().toLowerCase() + "/";
		TM  = new TransactionManager();

		loadItemsDB();
		loadPendingTransactions();
	}
	//public final LockManager LM = new LockManager();

	// Basic operations on RMItem //

	// Read a data item.
	protected RMItem readData(int tid, String key) {
		return TM.read(tid, key);
	}

	// Write a data item.
	protected void writeData(int tid, String key, RMItem value) {
		TM.write(tid, key, value);
	}

	// Remove the item out of storage.
	protected RMItem removeData(int tid, String key) {
		//RMItem value = TM.read(tid, key);
		TM.write(tid, key, null);
		return null; // We should not need to return something here TODO: verify that
	}


	// Basic operations on ReservableItem //

	// Delete the entire item.
	protected boolean deleteItem(int tid, String key) {
		Trace.info("RM::deleteItem(" + tid + ", " + key + ")");
		ReservableItem curObj = (ReservableItem) readData(tid, key);
		// Check if there is such an item in the storage.
		if (curObj == null) {
			Trace.warn("RM::deleteItem(" + tid + ", " + key + ") failed: "
					+ " item doesn't exist.");
			return false;
		} else {
			synchronized (curObj) {
				curObj = (ReservableItem) readData(tid, key);
				if (curObj == null) {
					Trace.warn("RM::deleteItem(" + tid + ", " + key + ") failed: "
							+ " item doesn't exist.");
					return false;
				}
				if (curObj.getReserved() == 0) {
					removeData(tid, curObj.getKey());
					Trace.info("RM::deleteItem(" + tid + ", " + key + ") OK.");
					return true;
				} else {
					Trace.info("RM::deleteItem(" + tid + ", " + key + ") failed: "
							+ "some customers have reserved it.");
					return false;
				}
			}
		}
	}

	// Query the number of available seats/rooms/cars.
	protected int queryNum(int tid, String key) {
		Trace.info("RM::queryNum(" + tid + ", " + key + ")");
		ReservableItem curObj = (ReservableItem) readData(tid, key);
		int value = 0;
		if (curObj != null) {
			synchronized (curObj) {
				curObj = (ReservableItem) readData(tid, key);
				if (curObj != null) {
					value = curObj.getCount();
				}
			}
		}
		Trace.info("RM::queryNum(" + tid + ", " + key + ") OK: " + value);
		return value;
	}

	// Query the price of an item.
	protected int queryPrice(int tid, String key) {
		Trace.info("RM::queryCarsPrice(" + tid + ", " + key + ")");
		ReservableItem curObj = (ReservableItem) readData(tid, key);
		int value = 0;
		if (curObj != null) {
			synchronized (curObj) {
				curObj = (ReservableItem) readData(tid, key);
				if (curObj != null) {
					value = curObj.getPrice();
				}
			}
		}
		Trace.info("RM::queryCarsPrice(" + tid + ", " + key + ") OK: $" + value);
		return value;
	}

	// Reserve an item.
	protected boolean reserveItem(int tid, int customerId,
								  String key, String location) {
		Trace.info("RM::reserveItem(" + tid + ", " + customerId + ", "
				+ key + ", " + location + ")");
		// Read customer object if it exists (and read lock it).
		Customer cust = (Customer) readData(tid, Customer.getKey(customerId));
		if (cust == null) {
			Trace.warn("RM::reserveItem(" + tid + ", " + customerId + ", "
					+ key + ", " + location + ") failed: customer doesn't exist.");
			return false;
		}
		synchronized (cust) {
			cust = (Customer) readData(tid, Customer.getKey(customerId));
			if (cust == null) {
				Trace.warn("RM::reserveItem(" + tid + ", " + customerId + ", "
						+ key + ", " + location + ") failed: customer doesn't exist.");
				return false;
			}
			// Check if the item is available.
			ReservableItem item = (ReservableItem) readData(tid, key);
			if (item == null) {
				Trace.warn("RM::reserveItem(" + tid + ", " + customerId + ", "
						+ key + ", " + location + ") failed: item doesn't exist.");
				return false;
			}
			synchronized (item) {
				item = (ReservableItem) readData(tid, key);
				if (item == null) {
					Trace.warn("RM::reserveItem(" + tid + ", " + customerId + ", "
							+ key + ", " + location + ") failed: item doesn't exist.");
					return false;
				}
				if (item.getCount() == 0) {
					Trace.warn("RM::reserveItem(" + tid + ", " + customerId + ", "
							+ key + ", " + location + ") failed: no more items.");
					return false;
				} else {
					// Do reservation.
					cust.reserve(key, location, item.getPrice());
					writeData(tid, cust.getKey(), cust);

					// Decrease the number of available items in the storage.
					item.setCount(item.getCount() - 1);
					item.setReserved(item.getReserved() + 1);

					Trace.warn("RM::reserveItem(" + tid + ", " + customerId + ", "
							+ key + ", " + location + ") OK.");
					return true;
				}
			}
		}
	}


	// Flight operations //

	@Override
	public String getName() {
		return "local-"+type.name();
	}

	// Create a new flight, or add seats to existing flight.
	// Note: if flightPrice <= 0 and the flight already exists, it maintains
	// its current price.
	@Override
	public boolean addFlight(int tid, int flightNumber,
							 int numSeats, int flightPrice) {
		Trace.info("RM::addFlight(" + tid + ", " + flightNumber
				+ ", $" + flightPrice + ", " + numSeats + ")");
		Flight curObj = (Flight) readData(tid, Flight.getKey(flightNumber));
		if (curObj == null) {
			// Doesn't exist; add it.
			Flight newObj = new Flight(flightNumber, numSeats, flightPrice);
			writeData(tid, newObj.getKey(), newObj);
			Trace.info("RM::addFlight(" + tid + ", " + flightNumber
					+ ", $" + flightPrice + ", " + numSeats + ") OK.");
		} else {
			synchronized (curObj) {
				curObj = (Flight) readData(tid, Flight.getKey(flightNumber));
				if (curObj == null) {
					Trace.info("RM::addFlight(" + tid + ", " + flightNumber
							+ ", $" + flightPrice + ", " + numSeats + "): "
							+ "concurrent modification error.");
					return false;
				}
				// Add seats to existing flight and update the price.
				curObj.setCount(curObj.getCount() + numSeats);
				if (flightPrice > 0) {
					curObj.setPrice(flightPrice);
				}
				writeData(tid, curObj.getKey(), curObj);
				Trace.info("RM::addFlight(" + tid + ", " + flightNumber
						+ ", $" + flightPrice + ", " + numSeats + ") OK: "
						+ "seats = " + curObj.getCount() + ", price = $" + flightPrice);
			}
		}
		return(true);
	}

	@Override
	public boolean deleteFlight(int tid, int flightNumber) {
		return deleteItem(tid, Flight.getKey(flightNumber));
	}

	// Returns the number of empty seats on this flight.
	@Override
	public int queryFlight(int tid, int flightNumber) {
		return queryNum(tid, Flight.getKey(flightNumber));
	}

	// Returns price of this flight.
	public int queryFlightPrice(int tid, int flightNumber) {
		return queryPrice(tid, Flight.getKey(flightNumber));
	}

	/*
	// Returns the number of reservations for this flight.
	public int queryFlightReservations(int id, int flightNumber) {
		Trace.info("RM::queryFlightReservations(" + id
				+ ", #" + flightNumber + ")");
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
				+ flightNumber + ")");
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
	public boolean addCars(int tid, String location, int numCars, int carPrice) {
		Trace.info("RM::addCars(" + tid + ", " + location + ", "
				+ numCars + ", $" + carPrice + ")");
		Car curObj = (Car) readData(tid, Car.getKey(location));
		if (curObj == null) {
			// Doesn't exist; add it.
			Car newObj = new Car(location, numCars, carPrice);
			writeData(tid, newObj.getKey(), newObj);
			Trace.info("RM::addCars(" + tid + ", " + location + ", "
					+ numCars + ", $" + carPrice + ") OK.");
		} else {
			synchronized (curObj) {
				curObj = (Car) readData(tid, Car.getKey(location));
				if (curObj == null) {
					Trace.info("RM::addCars(" + tid + ", " + location + ", "
							+ numCars + ", $" + carPrice + "): "
							+ "concurrent modification error.");
					return false;
				}
				// Add count to existing object and update price.
				curObj.setCount(curObj.getCount() + numCars);
				if (carPrice > 0) {
					curObj.setPrice(carPrice);
				}
				writeData(tid, curObj.getKey(), curObj);
				Trace.info("RM::addCars(" + tid + ", " + location + ", "
						+ numCars + ", $" + carPrice + ") OK: "
						+ "cars = " + curObj.getCount() + ", price = $" + carPrice);
			}
		}
		return(true);
	}

	// Delete cars from a location.
	@Override
	public boolean deleteCars(int tid, String location) {
		return deleteItem(tid, Car.getKey(location));
	}

	// Returns the number of cars available at a location.
	@Override
	public int queryCars(int tid, String location) {
		return queryNum(tid, Car.getKey(location));
	}

	// Returns price of cars at this location.
	@Override
	public int queryCarsPrice(int tid, String location) {
		return queryPrice(tid, Car.getKey(location));
	}


	// Room operations //

	// Create a new room location or add rooms to an existing location.
	// Note: if price <= 0 and the room location already exists, it maintains
	// its current price.
	@Override
	public boolean addRooms(int tid, String location, int numRooms, int roomPrice) {
		Trace.info("RM::addRooms(" + tid + ", " + location + ", "
				+ numRooms + ", $" + roomPrice + ")");
		Room curObj = (Room) readData(tid, Room.getKey(location));
		if (curObj == null) {
			// Doesn't exist; add it.
			Room newObj = new Room(location, numRooms, roomPrice);
			writeData(tid, newObj.getKey(), newObj);
			Trace.info("RM::addRooms(" + tid + ", " + location + ", "
					+ numRooms + ", $" + roomPrice + ") OK.");
		} else {
			synchronized (curObj) {
				curObj = (Room) readData(tid, Room.getKey(location));
				if (curObj == null) {
					Trace.info("RM::addRooms(" + tid + ", " + location + ", "
							+ numRooms + ", $" + roomPrice + "): "
							+ "concurrent modification error.");
					return false;
				}
				// Add count to existing object and update price.
				curObj.setCount(curObj.getCount() + numRooms);
				if (roomPrice > 0) {
					curObj.setPrice(roomPrice);
				}
				writeData(tid, curObj.getKey(), curObj);
				Trace.info("RM::addRooms(" + tid + ", " + location + ", "
						+ numRooms + ", $" + roomPrice + ") OK: "
						+ "rooms = " + curObj.getCount() + ", price = $" + roomPrice);
			}
		}
		return(true);
	}

	// Delete rooms from a location.
	@Override
	public boolean deleteRooms(int tid, String location) {
		return deleteItem(tid, Room.getKey(location));
	}

	// Returns the number of rooms available at a location.
	@Override
	public int queryRooms(int tid, String location) {
		return queryNum(tid, Room.getKey(location));
	}

	// Returns room price at this location.
	@Override
	public int queryRoomsPrice(int tid, String location) {
		return queryPrice(tid, Room.getKey(location));
	}


	// Customer operations //

	@Override
	public int newCustomer(int tid) {
		Trace.info("RM::newCustomer(" + tid + ")");
		// Generate a globally unique Id for the new customer.
		int customerId = Integer.parseInt(String.valueOf(tid) +
				String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
				String.valueOf(Math.round(Math.random() * 100 + 1)));
		Customer cust = new Customer(customerId);
		writeData(tid, cust.getKey(), cust);
		Trace.info("RM::newCustomer(" + tid + ") OK: " + customerId);
		return customerId;
	}

	// This method makes testing easier.
	@Override
	public boolean newCustomerId(int tid, int customerId) {
		Trace.info("RM::newCustomer(" + tid + ", " + customerId + ")");
		Customer cust = (Customer) readData(tid, Customer.getKey(customerId));
		if (cust == null) {
			cust = new Customer(customerId);
			writeData(tid, cust.getKey(), cust);
			Trace.info("RM::newCustomer(" + tid + ", " + customerId + ") OK.");
			return true;
		} else {
			Trace.info("RM::newCustomer(" + tid + ", " +
					customerId + ") failed: customer already exists.");
			return false;
		}
	}

	// Delete customer from the database.
	@Override
	public boolean deleteCustomer(int tid, int customerId) {
		Trace.info("RM::deleteCustomer(" + tid + ", " + customerId + ")");
		Customer cust = (Customer) readData(tid, Customer.getKey(customerId));
		if (cust == null) {
			Trace.warn("RM::deleteCustomer(" + tid + ", "
					+ customerId + ") failed: customer doesn't exist.");
			return false;
		} else {
			synchronized (cust) {
				cust = (Customer) readData(tid, Customer.getKey(customerId));
				if (cust == null) {
					Trace.warn("RM::deleteCustomer(" + tid + ", "
							+ customerId + ") failed: customer doesn't exist.");
					return false;
				}
				// Increase the reserved numbers of all reservable items that
				// the customer reserved.
				RMHashtable reservationHT = cust.getReservations();
				for (Enumeration e = reservationHT.keys(); e.hasMoreElements(); ) {
					String reservedKey = (String) (e.nextElement());
					ReservedItem reservedItem = cust.getReservedItem(reservedKey);
					Trace.info("RM::deleteCustomer(" + tid + ", " + customerId + "): "
							+ "deleting " + reservedItem.getCount() + " reservations "
							+ "for item " + reservedItem.getKey());
					ReservableItem item =
							(ReservableItem) readData(tid, reservedItem.getKey());
					synchronized (item) {
						item.setReserved(
								item.getReserved() - reservedItem.getCount());
						item.setCount(
								item.getCount() + reservedItem.getCount());
					}
					Trace.info("RM::deleteCustomer(" + tid + ", " + customerId + "): "
							+ reservedItem.getKey() + " reserved/available = "
							+ item.getReserved() + "/" + item.getCount());
				}
				// Remove the customer from the storage.
				removeData(tid, cust.getKey());
				Trace.info("RM::deleteCustomer(" + tid + ", " + customerId + ") OK.");
				return true;
			}
		}
	}

	// Return data structure containing customer reservation info.
	// Returns null if the customer doesn't exist.
	// Returns empty RMHashtable if customer exists but has no reservations.
	public RMHashtable getCustomerReservations(int tid, int customerId) {
		Trace.info("RM::getCustomerReservations(" + tid + ", "
				+ customerId + ")");
		Customer cust = (Customer) readData(tid, Customer.getKey(customerId));
		if (cust == null) {
			Trace.info("RM::getCustomerReservations(" + tid + ", "
					+ customerId + ") failed: customer doesn't exist.");
			return null;
		} else {
			synchronized (cust) {
				cust = (Customer) readData(tid, Customer.getKey(customerId));
				if (cust == null) {
					Trace.info("RM::getCustomerReservations(" + tid + ", "
							+ customerId + ") failed: customer doesn't exist.");
					return null;
				}
				return cust.getReservations();
			}
		}
	}

	// Return a bill.
	@Override
	public String queryCustomerInfo(int tid, int customerId) {
		Trace.info("RM::queryCustomerInfo(" + tid + ", " + customerId + ")");
		Customer cust = (Customer) readData(tid, Customer.getKey(customerId));
		if (cust == null) {
			Trace.warn("RM::queryCustomerInfo(" + tid + ", "
					+ customerId + ") failed: customer doesn't exist.");
			// Returning an empty bill means that the customer doesn't exist.
			return "";
		} else {
			String s = "";
			synchronized (cust) {
				cust = (Customer) readData(tid, Customer.getKey(customerId));
				if (cust == null) {
					Trace.warn("RM::queryCustomerInfo(" + tid + ", "
							+ customerId + ") failed: customer doesn't exist.");
					// Returning an empty bill means that the customer doesn't exist.
					return "";
				}
				s = cust.printBill();
			}
			Trace.info("RM::queryCustomerInfo(" + tid + ", " + customerId + "): \n");
			Trace.info(s);
			return s;
		}
	}

	// Add flight reservation to this customer.
	@Override
	public boolean reserveFlight(int tid, int customerId, int flightNumber) {
		return reserveItem(tid, customerId,
				Flight.getKey(flightNumber), String.valueOf(flightNumber));
	}

	// Add car reservation to this customer.
	@Override
	public boolean reserveCar(int tid, int customerId, String location, int numCars) {
		return reserveItem(tid, customerId, Car.getKey(location), location);
	}

	// Add room reservation to this customer.
	@Override
	public boolean reserveRoom(int tid, int customerId, String location, int numRooms) {
		return reserveItem(tid, customerId, Room.getKey(location), location);
	}


	// Reserve an itinerary.
	@Override
	public boolean reserveItinerary(int tid, int customerId, Vector flightNumbers,
									String location, int numCars, int numRooms) {
		return false;
	}

	/* Should not get called... the middleware decides on transaction IDs */
	@Override
	public int start() {
		Trace.info("RM::start()");
		return TM.start();
	}

	@Override
	public boolean start(int transactionId) {
		Trace.info(String.format("RM::start(%d)", transactionId));
		return (TM.start(transactionId) != null);
	}

	@Override
	public boolean commit(int transactionId) {
		Trace.info(String.format("RM::commit(%d)", transactionId));
		boolean toReturn = TM.commit(transactionId);

		storeItemsDB();

		return toReturn;
	}

	@Override
	public boolean commit2PC(int transactionId) {
		return false; // We should not be calling this on a LocalRM, it gets called on the Middleware's TM
	}

	@Override
	public boolean commitRequest(int transactionId) {
		Trace.info(String.format("RM::commitRequest(%d)", transactionId));
		boolean toReturn = TM.commitRequest(transactionId);

		storePendingTransactions();

		return toReturn;
	}

	@Override
	public boolean commitFinish(int transactionId) {
		boolean toReturn = TM.commitFinish(transactionId);

		storeItemsDB();

		return toReturn;
	}

	/* Not actually used in this implementation, only in MiddlewareResourceManager */
	@Override
	public boolean abort(int transactionId) {
		Trace.info(String.format("RM::abort(%d)", transactionId));
		return TM.abort(transactionId);
	}

	@Override
	public boolean shutdown() {
		Trace.info("RM::shutdown");
		active = false;
		return true;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	private boolean loadItemsDB() {
		SerializerHelper<RMHashtable> sh = new SerializerHelper<RMHashtable>(this.storagePath, "items");
		RMHashtable loaded = sh.loadFromFile();
		if (loaded != null) {
			this.TM.items = loaded;
			Trace.info(String.format("Loaded %d items from file", loaded.size()));
		}
		return (loaded != null);

	}

	private boolean storeItemsDB() {
		SerializerHelper<RMHashtable> sh = new SerializerHelper<RMHashtable>(this.storagePath, "items");
		return sh.saveToFile(this.TM.items);
	}

	private boolean loadPendingTransactions() {
		SerializerHelper<ArrayList<Transaction>> sh = new SerializerHelper<ArrayList<Transaction>>(this.storagePath, "txns");
		ArrayList<Transaction> loaded = sh.loadFromFile();
		if (loaded != null) {
			this.TM.loadPendingTransactions(loaded);
			Trace.info(String.format("Loaded %d pending transactions from file", loaded.size()));
		}
		return (loaded != null);
	}

	private boolean storePendingTransactions() {
		SerializerHelper<ArrayList<Transaction>> sh = new SerializerHelper<ArrayList<Transaction>>(this.storagePath, "txns");
		return sh.saveToFile(this.TM.getPendingTransactions());
	}



}
