// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package system;

import java.io.*;


// Resource manager data item.

public abstract class RMItem implements Serializable, Cloneable {

	RMItem() {
		super();
	}

	public abstract RMItem copy();
}

