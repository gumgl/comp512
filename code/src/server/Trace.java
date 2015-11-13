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
        String callingClass = new Exception().getStackTrace()[2].getClassName();
        String threadID = Thread.currentThread().getName();
        return threadID + "[" + callingClass + "]";
    }

    private static void printMessage(PrintStream o, String name, String msg) {
        o.println(name + " " + getStatus() + " " + msg);
    }

    public static void info(String msg) {
        if (printInfo)
            printMessage(System.out, " INFO", msg);
    }
    
    public static void warn(String msg) {
        if (printWarnings)
            printMessage(System.out, " WARN", msg);
    }
    
    public static void error(String msg) {
        if (printErrors)
            printMessage(System.err, "ERROR", msg);
    }
    
}
