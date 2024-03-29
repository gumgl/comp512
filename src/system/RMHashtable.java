// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package system;

import java.util.*;


// A specialization of Hashtable with some extra diagnostics.

public class RMHashtable extends Hashtable<String, RMItem> {

	public RMHashtable() {
	  super();
	}

	public String toString() {
		String s = "RMHashtable { \n";
		Object key = null;
		for (Enumeration e = keys(); e.hasMoreElements();) {
			key = e.nextElement();
			String value = get(key).toString();
			s = s + "  [key = " + key + "] " + value + "\n";
		}
		s = s + "}";
		return s;
	}

	public RMHashtable copy() {
		RMHashtable cpy = new RMHashtable();

		forEach((k, v) -> cpy.put(k, v));

		return cpy;
	}

	public void dump() {
		System.out.println(toString());
	}

}
