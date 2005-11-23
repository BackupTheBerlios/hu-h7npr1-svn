package javaappletth7npr1rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface CijferlijstService
extends Remote
{
  String refName = "CijferlijstService";
  
  Hashtable getStudents()
  throws RemoteException;
  
  boolean store(String StudentNaam, double Cijfer)
  throws RemoteException;
}
