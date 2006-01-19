package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface CijferlijstService
extends Remote
{
	String refName = "CijferlijstService";

	String[][] getList()                                 throws RemoteException;
	String[]   getOne(int id)                            throws RemoteException;
	void       setOne(int id, String name, double grade) throws RemoteException;
	void       deleteOne(int id)                         throws RemoteException;
}