package pi;

import java.rmi.*;

public interface Pi
extends Remote
{
  String refName = "PI";
  Result perform(Task t) throws RemoteException;
}