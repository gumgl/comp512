package helper;

import java.io.IOException;
import java.net.Socket;

public class RepairConnection
{
	String ip;
	int port;
	
	/**
	 * This class will try again and again to repair the connection with a lost RM
	 * @param ipAddress: ip adress of the RM
	 * @param portNumber: port number of the RM
	 */
	public RepairConnection(String ipAddress, int portNumber)
	{
		ip=ipAddress;
		port=portNumber;
	}
	
	/**
	 * Creates the new socket when the connection is back
	 * @return the newly created socket for the RM.
	 */
	public Socket getNewConnection()
	{
		Socket newSocket = null;
		boolean keepLooping = true;
		
		while(keepLooping)
		{
			try {
				newSocket = new Socket(ip, port);
				keepLooping = false;
			} catch (IOException e) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		return newSocket;
	}
}
