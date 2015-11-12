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

    Socket serverSocket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    public SocketSender(InetAddress host, int port) {
        Trace.info(String.format("Connecting to server %s:%d... ",host.toString(),port));
        while(true) {
            try {
                serverSocket = new Socket(host, port);
                Trace.info("Connected!");
                outStream = new ObjectOutputStream(serverSocket.getOutputStream());
                //System.out.println("[SocketSender]OutputStream created");
                outStream.flush();
                //System.out.println("[SocketSender]OutputStream flushed");
                inStream = new ObjectInputStream(serverSocket.getInputStream());

                break; // Successful connection, break out of loop
            } catch(IOException e){
                Trace.error("Failed, trying again...");
            }
        }
    }

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        Trace.info("Invoking target." + invocation.toString());
        outStream.writeObject(invocation);
        Trace.info("Sent! Waiting for response...");
        Response response = (Response) inStream.readObject();
        Trace.info("Response received: " + response.toString());

        if (response.getException() != null)
            throw response.getException();
        else
            return response.getReturnValue();
    }
}
