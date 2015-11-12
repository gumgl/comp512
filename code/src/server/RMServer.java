package server;


import system.*;
import system.ResourceManager;

import java.io.IOException;

public class RMServer extends Server {
    public final Class type;

    public static void main(String[] args) throws Exception {

        // Get the right port and RM type:
        String stringType = args[0];
        Class classType;
        String stringPort;

        if (stringType.equals("flight")) {
            classType = system.Flight.class;
            stringPort = args[1];
        } else if (stringType.equals("car")) {
            classType = system.Car.class;
            stringPort = args[2];
        } else /*if (stringType.equals("room"))*/ {
            classType = system.Room.class;
            stringPort = args[3];
        }
        int port = Integer.parseInt(stringPort);

        Trace.info(String.format("%s-type Server starting on port %s\n", stringType, stringPort));

        Server server = new RMServer(port, classType);
        server.run();
    }

    public RMServer(int port, Class type) throws IOException {
        super(port);
        this.type = type;
    }

    @Override
    protected ResourceManager setupResourceManager() {
        return new LocalResourceManager();
    }

}
