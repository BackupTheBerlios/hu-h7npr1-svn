import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationSystem;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Main
{
	private static boolean persistentRMIregistry = false;

	public static void main(String[] args)
	throws Exception
	{
		System.setProperty("java.rmi.server.codebase", "http://127.0.0.1:4711/");

		ActivationSystem activationSystem = getSystem();
		ActivationGroupDesc agd = new ActivationGroupDesc(null, null);
		ActivationGroupID agid = activationSystem.registerGroup(agd);
		String className = "pi.PiImpl";
		// String location = "file://home/ebeukhof/Documents/HvU/H7NPR1/Sources/Projects/PI_Activated/Server/";
		String location = "http://127.0.0.1:4711/";
		ActivationDesc ad = new ActivationDesc(agid, className, location, null);
		Remote pi = Activatable.register(ad);

		if (persistentRMIregistry)
		{
			Naming.rebind("Pi", pi);
			System.out.println("rebind in rmiregistry");
		}
		else
		{
			System.out.println(writeReference(pi, "PiRef"));
		}
	}

	private static ActivationSystem getSystem()
	{
		System.out.println("getActivationSystem()");
		for (int i=0; i<10; i++)
		{
			try
			{
				return ActivationGroup.getSystem();
			}
			catch (Exception e)
			{
				System.out.println(e);
				try { Thread.sleep(1000); }
				catch (InterruptedException ie) {}
			}
		}
		System.out.println("rmid not started");
		System.exit(1);
		return null;
	}

	private static String writeReference(Object inObj, String name)
	{
		try
		{
			String refBase = "/home/ebeukhof/Documents/HvU/H7NPR1/Sources/Projects/PI_Activated/Client/";
			FileOutputStream fos = new FileOutputStream(refBase + name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(inObj);
			oos.close();
			return "reference stored on disk";
		}
		catch (Exception e)
		{
			return "reference storage failed";
		}
	}
}