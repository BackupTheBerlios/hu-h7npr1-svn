/******************************************************************
 * CODE FILE   : CijferlijstService.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-01-2006
 * Beschrijving: Interface CijferlijstService - Interface to Cijfer-
 *               lijstServiceImpl
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CijferlijstService
extends Remote
{
	String refName = "CijferlijstService";

	String[][] getList()                                 throws RemoteException;
	String[]   getOne(int id)                            throws RemoteException;
	void       setOne(int id, String name, double grade) throws RemoteException;
	void       deleteOne(int id)                         throws RemoteException;
}