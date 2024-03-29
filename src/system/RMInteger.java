// -------------------------------
// Adapted Kevin T. Manley
// CSE 593
// -------------------------------

package system;

import java.io.*;


// A simple Integer wrapper.

public class RMInteger extends RMItem implements Serializable {

    protected int m_value;

    public RMInteger(int value) {
        m_value=value;
    }

    public int getValue() {
        return m_value;
    }

    public void setValue(int value) {
        m_value = value;
    }

    public String toString() {
        return String.valueOf(m_value);
    }

	@Override
    public RMInteger copy() {
        return new RMInteger(m_value);
    }
}
