package middleware;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionWithClient extends Thread
{
	private Socket clientConnection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Main mainClass;
	
	
	public ConnectionWithClient(Main m, Socket connection) throws Exception
	{
		mainClass = m;
		clientConnection = connection;
		output = new ObjectOutputStream(clientConnection.getOutputStream());
	    output.flush(); //sometimes there are leftover data
	    input = new ObjectInputStream(clientConnection.getInputStream());
	}
	
	@Override
	public void run()
	{
		String message = "";
		String[] args;
		
		while(true)
		{
			try
		    {
				message = (String)input.readObject();
				args = message.split(",");
				output.writeObject(messageToSend(findChoice(args[0]), message));
			    output.flush();
		    }
		    catch(ClassNotFoundException classNotFoundException)
		    {
		        System.out.println("not a String");
		    }
			catch (IOException e)
			{  
				System.out.println("Connection Lost...");
				break;
			}
		}
	}
	
	private String messageToSend(int code, String message) throws ClassNotFoundException, IOException
	{
		String toReturn = "";
		switch(code) {
        
        case 2:  //new flight
        	toReturn = mainClass.airplaneRMconnection.functionCall(message);
            break;
            
        case 3:  //new car
            toReturn = mainClass.carRMconnection.functionCall(message);
            break;
            
        case 4:  //new room
            toReturn = mainClass.hotelRMconnection.functionCall(message);
            break;
            
        case 5:  //new Customer
            toReturn = mainClass.customerRMconnection.functionCall(message);
            String customMessage = "newcustomid,1,"+toReturn;
            mainClass.carRMconnection.functionCall(customMessage);
            mainClass.airplaneRMconnection.functionCall(customMessage);
            mainClass.hotelRMconnection.functionCall(customMessage);
            break;
            
        case 6: //delete Flight
            toReturn = mainClass.airplaneRMconnection.functionCall(message);
            break;
            
        case 7: //delete car
            toReturn = mainClass.carRMconnection.functionCall(message);
            break;
            
        case 8: //delete room
            toReturn = mainClass.hotelRMconnection.functionCall(message);
            break;
            
        case 9: //delete Customer
        	toReturn = mainClass.customerRMconnection.functionCall(message);
            mainClass.carRMconnection.functionCall(message);
            mainClass.airplaneRMconnection.functionCall(message);
            mainClass.hotelRMconnection.functionCall(message);
            break;
            
        case 10: //querying a flight
            toReturn = mainClass.airplaneRMconnection.functionCall(message);
            break;
            
        case 11: //querying a car Location
            toReturn = mainClass.carRMconnection.functionCall(message);
            break;
            
        case 12: //querying a room location
            toReturn = mainClass.hotelRMconnection.functionCall(message);
            break;
            
        case 13: //querying Customer Information
        	toReturn = mainClass.carRMconnection.functionCall(message)
              + mainClass.airplaneRMconnection.functionCall(message)
              + mainClass.hotelRMconnection.functionCall(message);
            break;               
            
        case 14: //querying a flight Price
            toReturn = mainClass.airplaneRMconnection.functionCall(message);
            break;
            
        case 15: //querying a car Price
            toReturn = mainClass.carRMconnection.functionCall(message);             
            break;

        case 16: //querying a room price
            toReturn = mainClass.hotelRMconnection.functionCall(message);
            break;
            
        case 17:  //reserve a flight
        	toReturn = mainClass.airplaneRMconnection.functionCall(message);
            break;
            
        case 18:  //reserve a car
        	toReturn = mainClass.carRMconnection.functionCall(message);
            break;
            
        case 19:  //reserve a room
        	toReturn = mainClass.hotelRMconnection.functionCall(message);
            break;
            
        case 20:  //reserve an Itinerary
        	toReturn = Boolean.toString(false);
            break;
            
        case 22:  //new Customer given id
        	toReturn = mainClass.customerRMconnection.functionCall(message);
            customMessage = "newcustom,1,"+toReturn;
            mainClass.carRMconnection.functionCall(customMessage);
            mainClass.airplaneRMconnection.functionCall(customMessage);
            mainClass.hotelRMconnection.functionCall(customMessage);
            break;
            
        default:
            System.out.println("The interface does not support this command.");
            break;
        }
		
		return toReturn;
	}
	
	public int findChoice(String argument)
	{
        if (argument.compareToIgnoreCase("help") == 0)
            return 1;
        else if (argument.compareToIgnoreCase("newflight") == 0)
            return 2;
        else if (argument.compareToIgnoreCase("newcar") == 0)
            return 3;
        else if (argument.compareToIgnoreCase("newroom") == 0)
            return 4;
        else if (argument.compareToIgnoreCase("newcustomer") == 0)
            return 5;
        else if (argument.compareToIgnoreCase("deleteflight") == 0)
            return 6;
        else if (argument.compareToIgnoreCase("deletecar") == 0)
            return 7;
        else if (argument.compareToIgnoreCase("deleteroom") == 0)
            return 8;
        else if (argument.compareToIgnoreCase("deletecustomer") == 0)
            return 9;
        else if (argument.compareToIgnoreCase("queryflight") == 0)
            return 10;
        else if (argument.compareToIgnoreCase("querycar") == 0)
            return 11;
        else if (argument.compareToIgnoreCase("queryroom") == 0)
            return 12;
        else if (argument.compareToIgnoreCase("querycustomer") == 0)
            return 13;
        else if (argument.compareToIgnoreCase("queryflightprice") == 0)
            return 14;
        else if (argument.compareToIgnoreCase("querycarprice") == 0)
            return 15;
        else if (argument.compareToIgnoreCase("queryroomprice") == 0)
            return 16;
        else if (argument.compareToIgnoreCase("reserveflight") == 0)
            return 17;
        else if (argument.compareToIgnoreCase("reservecar") == 0)
            return 18;
        else if (argument.compareToIgnoreCase("reserveroom") == 0)
            return 19;
        else if (argument.compareToIgnoreCase("itinerary") == 0)
            return 20;
        else if (argument.compareToIgnoreCase("quit") == 0)
            return 21;
        else if (argument.compareToIgnoreCase("newcustomerid") == 0)
            return 22;
        else
            return 666;
    }
}
