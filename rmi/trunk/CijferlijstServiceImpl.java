package javaappletth7npr1rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

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
  
  public Hashtable getStudents()
  throws RemoteException
  {
      return cijfers.getHashtable();
  }

  public boolean store(String StudentNaam, double Cijfer)
  throws RemoteException
  {     
      return cijfers.store(StudentNaam, Cijfer);
  }
}
