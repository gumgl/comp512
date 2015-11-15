// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package system;

import java.io.*;


// Superclass for the three reservable items, Flight, Car, and Room.

public abstract class ReservableItem extends RMItem implements Serializable {

    protected int m_nCount;
    protected int m_nPrice;
    protected int m_nReserved;
    protected String m_strLocation;

    public ReservableItem() {
        m_nCount = 0;
        m_nPrice = 0;
        m_nReserved = 0;
        m_strLocation = "";
    }
    
    public ReservableItem(String location, int count, int price) {
        super();
        m_strLocation = location;
        m_nCount = count;
        m_nPrice = price;
        m_nReserved = 0;
    }

    protected void copyInternals(ReservableItem item) {
        this.m_nCount = item.m_nCount;
        this.m_nPrice = item.m_nPrice;
        this.m_nReserved = item.m_nReserved;
        this.m_strLocation = item.m_strLocation + "";
    }

    public void setCount(int count) { 
        m_nCount = count; 
    }

    public int getCount() { 
        return m_nCount;
    }

    public void setPrice(int price) { 
        m_nPrice = price;
    }

    public int getPrice() { 
        return m_nPrice; 
    }

    public void setReserved(int r) {
        m_nReserved = r; 
    }

    public int getReserved() { 
        return m_nReserved; 
    }

    public String getLocation() { 
        return m_strLocation;
    }

    public String toString() {
        return "ReservableItem key = " + getKey() + ", " 
                + "location = " + getLocation() + ", "
                + "count = " + getCount() + ", price = " + getPrice(); 
    }

    public abstract String getKey();

}
