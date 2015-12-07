package helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;

public class SerializerHelper<E extends Serializable> 
{
	//change this to match your path...you can also just call the constructor that has 2 parameters
	private String defaultFolderPath = "E:\\USB\\School\\McGill\\classes\\Comp512\\Assignments\\project3\\tests\\";
	
	String fileName;
	
	private File theFile = null;
	
	public SerializerHelper(String fileName)
	{
		this.fileName = fileName;
		theFile = new File(defaultFolderPath+fileName);
	}
	
	public SerializerHelper(String folderPath, String fileName)
	{
		this.fileName = fileName;
		defaultFolderPath = folderPath;
		theFile = new File(defaultFolderPath+fileName);
	}
	
	public boolean doesFileExist()
	{
		return theFile.exists();
	}
	
	public boolean saveToFile(E objectToSave)
	{
		try
		(
			OutputStream file = new FileOutputStream(defaultFolderPath+fileName);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
		){
			output.writeObject(objectToSave);
		}
		catch(IOException e)
		{
			System.out.println("ERROR (001): Failed to save file.");
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public E loadFromFile()
	{
		try
		(
			InputStream file = new FileInputStream(defaultFolderPath+fileName);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
		){
			return (E)input.readObject();
		}
		catch(ClassNotFoundException e)
		{
			//System.out.println("ERROR (002): Failed to load file.");
		}
		catch(IOException e)
		{
			//System.out.println("ERROR (003): Failed to load file.");
		}
		
		return null;
	}
	
	public void deleteFile()
	{
		try {
			Files.deleteIfExists(theFile.toPath());
		} catch (IOException e) {
			System.out.println("ERROR (004): Failed to delete file.");
		}
	}
}
