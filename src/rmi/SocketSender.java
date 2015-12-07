package rmi;

import server.Trace;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/*
 * Helper class for RMIResourceManager
 */

public class SocketSender implements Invokable {
	InetAddress host;
	int port;
	Socket socket;
	ObjectOutputStream outStream;
	ObjectInputStream inStream;
	private boolean available = false;

	public SocketSender(InetAddress host, int port) {
		this.host = host;
		this.port = port;

		this.socket = setupSocket();
	}

	private Socket setupSocket() {
		Trace.info(String.format("Connecting to server %s:%d... ",host.toString(),port));
		while(true) {
			try {
				Socket newSocket = new Socket(this.host, this.port);
				this.available = true;
				Trace.info("Connected!");
				outStream = new ObjectOutputStream(newSocket.getOutputStream());
				//System.out.println("[SocketSender]OutputStream created");
				outStream.flush();
				//System.out.println("[SocketSender]OutputStream flushed");
				inStream = new ObjectInputStream(newSocket.getInputStream());
				return newSocket; // Successful connection, break out of loop
			} catch (IOException e) { // Retry until success...
				Trace.error("Failed, trying again...", e);
				this.available = false;
			}
		}
	}

	@Override
	public Object invoke(Invocation invocation) throws RuntimeException {
		Trace.info("Invoking target." + invocation.toString());

		while (true) { // As many attempts as it takes to perform the Invocation
			try {
				synchronized (outStream) {
					outStream.writeObject(invocation);
				}
				Trace.info("Sent! Waiting for response...");
				Response response;
				synchronized (inStream) {
					response = (Response) inStream.readObject();
				}
				Trace.info("Response received: " + response.toString());

				if (response.getException() != null)
					throw response.getException();
				else
					return response.getReturnValue();
			} catch (IOException e) {
				Trace.error("Error with socket, reconnecting...", e);
				this.available = false;
				this.socket = setupSocket();
				//throw new UncheckedIOException(e);
			} catch (ClassNotFoundException e) {
				Trace.error("Problem instantiating response");
				e.printStackTrace();
				throw new ClassCastException();
			}
		}
	}

	public boolean isAvailable() {
		return this.available;
	}
}
