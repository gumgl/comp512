package server;


import system.*;
import system.IResourceManager;

import java.io.IOException;

public class RMServer extends Server {
    public final Class type;

    public static void main(String[] args) throws Exception {


        String stringType = args[0];
        Class classType;
        String stringPort;



        if (stringType.equals("flight")) {
            classType = Flight.class;
            stringPort = args[1];
        } else if (stringType.equals("car")) {
            classType = Car.class;
            stringPort = args[2];
        } else /*if (stringType.equals("room"))*/ {
            classType = Room.class;
            stringPort = args[3];
        }
        int port = Integer.parseInt(stringPort);

        System.out.printf("%s-type Server starting on port %s\n", stringType, stringPort);

        Server server = new RMServer(port, classType);
        server.run();
    }

    public RMServer(int port, Class type) throws IOException {
        super(port);
        this.type = type;
    }

    @Override
    protected IResourceManager setupResourceManager() {
        return new LocalResourceManager();
    }

    @Override
    public boolean shutdown() {
        return false;
    }
}
