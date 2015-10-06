package middleware;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class RMconnection
{
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	
	public RMconnection(String host, int port) throws Exception
	{
		connection = new Socket(InetAddress.getByName(host), port);
		output = new ObjectOutputStream(connection.getOutputStream());
	    output.flush();
	    input = new ObjectInputStream(connection.getInputStream());
	}
	
	public String functionCall(String message) throws IOException, ClassNotFoundException
	{
		try {
			
			output.writeObject(message);
			output.flush();
			
			try
			{
				return (String)input.readObject();
			}
			catch (ClassNotFoundException e)
			{
				System.out.println("message was not received.");
				throw e;
			}
			
		} catch (IOException e) {
			
			System.out.println("Message was not sent.");
			throw e;
		}
	    
	}
}
