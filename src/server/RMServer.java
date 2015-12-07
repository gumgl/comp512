package server;


import com.sun.media.sound.JARSoundbankReader;
import system.*;
import system.ResourceManager;

import java.io.IOException;

public class RMServer extends Server {
	public final ResourceManager.Type type;
	LocalResourceManager localRM;

	public static void main(String[] args) throws Exception {

		// Get the right port and RM type:
		String stringType = args[0];
		ResourceManager.Type type;
		String stringPort;

		if (stringType.equals("flight")) {
			type = ResourceManager.Type.FLIGHT;
			stringPort = args[1];
		} else if (stringType.equals("car")) {
			type = ResourceManager.Type.CAR;
			stringPort = args[2];
		} else /*if (stringType.equals("room"))*/ {
			type = ResourceManager.Type.ROOM;
			stringPort = args[3];
		}
		int port = Integer.parseInt(stringPort);

		Trace.info(String.format("%s-type Server starting on port %s\n", stringType, stringPort));

		RMServer server = new RMServer(port, type);
		server.start();
	}

	public RMServer(int port, ResourceManager.Type type) throws IOException {
		super(port);
		this.type = type;
		localRM = new LocalResourceManager(type);
	}

	@Override
	public ResourceManager setupRM() {
		return localRM;
	}
}
