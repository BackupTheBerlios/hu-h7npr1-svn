package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import interfaces.*;

public class CijferlijstServiceImpl
extends UnicastRemoteObject
implements CijferlijstService
{
	private static int port = 10099;
	private Cijferlijst cijfers;

	public CijferlijstServiceImpl()
	throws RemoteException
	{
		super(port);       
		cijfers = new Cijferlijst();
	}

	public String[][] getList()
	throws RemoteException
	{
		CijferlijstData[] dataArray = cijfers.getCijferlijst();
		int rowCount = 0;
		if (dataArray != null)
			rowCount = dataArray.length;
		String[][] entries = new String[rowCount][];
		for (int index = 0; index < rowCount; index++)
		{
			entries[index] = new String[]{String.valueOf(dataArray[index].cijferID),
													dataArray[index].studentName,
													String.valueOf(dataArray[index].cijfer)};
		}
		return entries;
	}
	
	public String[] getOne(int id)
	throws RemoteException
	{
		CijferlijstData cijfer = cijfers.getCijfer(id);
		if (cijfer != null)
		{
			String[] entry = new String[]{String.valueOf(cijfer.cijferID),
													cijfer.studentName,
													String.valueOf(cijfer.cijfer)};
			return entry;
		}
		return null;
	}
	
	public void setOne(int id, String name, double grade)
	throws RemoteException
	{
		CijferlijstData cijfer = new CijferlijstData();
		cijfer.cijferID = id;
		cijfer.studentName = name;
		cijfer.cijfer = grade;
		cijfers.setCijfer(cijfer);
	}

	public void deleteOne(int id)
	throws RemoteException
	{
		cijfers.deleteCijfer(id);
	}

	private String[][] arrayAdd(String[][] inArray, String[] addData)
	{
		int arraySize = 0;
		if (inArray != null)
			arraySize = inArray.length;
		if (addData == null)
			return inArray;
		String[][] outArray = new String[arraySize+1][];
		for (int index = 0; index < arraySize; index++)
		{
			outArray[index] = inArray[index];
		}
		outArray[arraySize] = addData;
		return outArray;
	}
}
