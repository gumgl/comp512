package rmi;

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
        System.out.printf("[SocketSender] Connecting to server %s:%d... ",host.toString(),port);
        while(true) {
            try {
                serverSocket = new Socket(host, port);
                System.out.println("Connected!");
                outStream = new ObjectOutputStream(serverSocket.getOutputStream());
                //System.out.println("[SocketSender]OutputStream created");
                outStream.flush();
                //System.out.println("[SocketSender]OutputStream flushed");
                inStream = new ObjectInputStream(serverSocket.getInputStream());

                break; // Successful connection, break out of loop

            } catch(IOException e){
                System.err.printf("\n[SocketSender] Failed, trying again...");
            }
        }
    }

    @Override
    public Object invoke(Invocation invocation) throws Exception {
        System.out.println("[SocketSender] Invoking target." + invocation.toString());
        outStream.writeObject(invocation);
        System.out.println("[SocketSender] Sent! Waiting for response...");
        Response response = (Response) inStream.readObject();
        System.out.println("[SocketSender] Response received: " + response.toString());

        if (response.getException() != null)
            throw response.getException();
        else
            return response.getReturnValue();
    }
}
