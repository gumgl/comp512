// -------------------------------
// Kevin T. Manley
// CSE 593
// -------------------------------

package system;

import java.util.*;


public class Customer extends RMItem {

    private int m_nID;
    private RMHashtable m_Reservations;

    Customer(int customerId) {
        super();
        m_nID = customerId;
        m_Reservations = new RMHashtable();
    }

    public void setId(int customerId) { 
        m_nID = customerId; 
    }

    public int getId() { 
        return m_nID; 
    }

    public void reserve(String key, String location, int price) {
        ReservedItem reservedItem = getReservedItem(key);
        if (reservedItem == null) {
            // Customer doesn't already have a reservation for this resource, 
            // so create a new one now.
            reservedItem = new ReservedItem(key, location, 1, price);
        } else {
            reservedItem.setCount(reservedItem.getCount() + 1);
            // Note: latest price overrides existing price.
            reservedItem.setPrice(price);
        }
        m_Reservations.put(reservedItem.getKey(), reservedItem);
    }

    public ReservedItem getReservedItem(String key) {
        ReservedItem reservedItem = (ReservedItem) m_Reservations.get(key);
        return reservedItem;
    }

    public String printBill() {
        String s = "Bill for customer " + m_nID + " { \n";
        Object key = null;
        for (Enumeration e = m_Reservations.keys(); e.hasMoreElements();) {
            key = e.nextElement();
            ReservedItem item = (ReservedItem) m_Reservations.get(key);
            s = s + item.getCount() + " " + item.getReservableItemKey() 
                    + " $" + item.getPrice() + "\n";
        }
        s = s + "}";
        return s;
    }

    public String toString() { 
        return "Customer key = " + getKey() + ", id = " + getId() + ", " 
                + "reservations: \n" + m_Reservations.toString() + "\n";
    }

    public static String getKey(int customerId) {
        String s = "customer-" + customerId;
        return s.toLowerCase();
    }

    public String getKey() {
        return Customer.getKey(getId());
    }

    @Override
    public Customer copy() {
        Customer cpy = new Customer(m_nID);
        cpy.m_Reservations = (RMHashtable) m_Reservations.copy();
        return cpy;
    }

    public RMHashtable getReservations() {
        try {
            return (RMHashtable) m_Reservations;
        } catch (Exception e) {
            return null;
        }
    }

}
