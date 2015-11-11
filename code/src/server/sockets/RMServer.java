package server.sockets;


import server.*;
import server.ws.ResourceManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

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
    ResourceManager setupResourceManager() {
        return new ResourceManagerImpl();
    }
}