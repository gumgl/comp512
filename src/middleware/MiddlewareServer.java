package middleware;

import rmi.Receiver;
import rmi.SocketSender;
import server.RMIResourceManager;
import server.Server;
import server.Trace;
import system.LocalResourceManager;
import system.ResourceManager;
import transactions.Transaction;
import transactions.TransactionManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiddlewareServer extends Server {
	/* Simply for setup so we have access to them in setupResourceManager() */
	ArrayList<InetAddress> RMaddresses;
	ArrayList<Integer> RMports;
	LocalResourceManager customerRM;
	//TransactionManager TM;

	public static void main(String[] args) throws Exception {
		int offset = 1;
		int numRMs = 3;

		int port = Integer.parseInt(args[0]);

		ArrayList<InetAddress> RMaddresses = new ArrayList<InetAddress>();
		ArrayList<Integer> RMports = new ArrayList<Integer>();

		for (int i=offset; i<offset+numRMs*2; i += 2) {
			RMaddresses.add(InetAddress.getByName(args[i]));
			RMports.add(Integer.parseInt(args[i+1]));
		}

		MiddlewareServer server = new MiddlewareServer(port, RMaddresses, RMports);
		server.start();
	}

	public MiddlewareServer(int port, ArrayList<InetAddress> RMaddresses, ArrayList<Integer> RMports) throws IOException {
		super(port);
		this.customerRM = new LocalResourceManager();
		//this.TM = new TransactionManager();
		// Store addresses and ports so that when Server calls setupRM, we have access to them
		this.RMaddresses = RMaddresses;
		this.RMports = RMports;
	}

	@Override
	public ResourceManager setupRM() {
		Trace.info("Connecting to individual RMs...");
		// Every client creates a new thread with new network-RMs but the same customer RM
		return new MiddlewareResourceManager(
				new RMIResourceManager(new SocketSender(RMaddresses.get(0), RMports.get(0))),
				new RMIResourceManager(new SocketSender(RMaddresses.get(1), RMports.get(1))),
				new RMIResourceManager(new SocketSender(RMaddresses.get(2), RMports.get(2))),
				this.customerRM);
	}
}
