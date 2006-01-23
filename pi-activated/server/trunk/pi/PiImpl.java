package pi;

import java.rmi.*;
import java.rmi.activation.*;
import java.rmi.server.*;

public class PiImpl
extends Activatable
implements Pi, Unreferenced
{
	public PiImpl(ActivationID id, MarshalledObject data)
	throws RemoteException
	{
		super(id, 0);
		System.out.println("IpImpl("+id+", "+data+")");
	}

	public Result perform(Task t)
	throws RemoteException
	{
		System.out.println("perform(" + t + ")");
		return t.execute();
	}

	public void unreferenced()
	{
		System.out.println("PiImpl.unreferenced()");
		try
		{
			if (!Activatable.inactive(getID()))
			{
				System.out.println("Activatable.inactive(getID)) is false");
			}
		}
		catch (ActivationException ae) { System.out.println(ae); }
		catch (RemoteException re) { System.out.println(re); }
	}	    
}