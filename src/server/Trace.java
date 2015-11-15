// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package server;


// A simple wrapper around System.out.println, allows us to disable some of
// the verbose output from RM, TM, and WC if we want.

import java.io.PrintStream;

public class Trace {

	private static final boolean printInfo = true;
	private static final boolean printWarnings = true;
	private static final boolean printErrors = true;

	private static String getStatus() {
		StackTraceElement ste = new Exception().getStackTrace()[3];
		String callingClass = (ste == null) ? "Anonymous" : ste.getClassName();
		String threadID = Thread.currentThread().getName();
		return threadID + "[" + callingClass + "]";
	}

	private static void printMessage(PrintStream o, String name, String msg) {
		if (o != null && !o.checkError())
			o.println(name + " " + getStatus() + " " + msg);
	}

	public static void info(String msg) {
		if (printInfo)
			printMessage(System.out, " INFO", msg);
	}

	public static void info(String msg, Throwable e) {
		if (printInfo)
			printMessage(System.out, " INFO", msg + ": " + throwableToString(e));
	}

	public static void warn(String msg) {
		if (printWarnings)
			printMessage(System.out, " WARN", msg);
	}

	public static void error(String msg) {
		if (printErrors)
			printMessage(System.err, "ERROR", msg);
	}

	public static void error(Exception e) {
		if (printErrors)
			printMessage(System.err, "ERROR", throwableToString(e));
	}

	public static void error(String msg, Throwable e) {
		if (printErrors)
			printMessage(System.err, "ERROR", msg + ": " + throwableToString(e));
	}

	public static String throwableToString(Throwable e) {
		return ((e != null) ? e.getClass().getName() : "null")
				+ "(" + e.getMessage() + ")";
	}

}
