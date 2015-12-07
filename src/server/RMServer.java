package server;


import middleware.MiddlewareResourceManager;
import rmi.Receiver;
import rmi.SocketSender;
import system.*;
import system.ResourceManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

public class RMServer extends Server {
	public final Class type;
	LocalResourceManager localRM;

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

		RMServer server = new RMServer(port, classType);
		server.start();
	}

	public RMServer(int port, Class type) throws IOException {
		super(port);
		this.type = type;
		localRM = new LocalResourceManager();
	}

	@Override
	public ResourceManager setupRM() {
		return localRM;
	}
}
