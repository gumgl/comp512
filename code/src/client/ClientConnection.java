package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientConnection
{
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	
	public ClientConnection(String host, int port)
	{		
		try
		{
			connection = new Socket(InetAddress.getByName(host), port);
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			System.out.println("connected to server.");
		}
		catch(IOException exception)
		{
			System.out.println("Failed to connect: "+exception.getMessage());
		}
	}
	
	public String callFunction(String message) throws ClassNotFoundException, IOException
	{
		output.writeObject(message);
		output.flush();
		
		
		return (String) input.readObject();
	}
}
