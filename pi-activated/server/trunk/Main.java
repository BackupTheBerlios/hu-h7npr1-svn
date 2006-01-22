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
    System.setProperty("java.rmi.server.codebase", "http://schooltest/");

    ActivationSystem activationSystem = getSystem();
    ActivationGroupDesc agd = new ActivationGroupDesc(null, null);
    ActivationGroupID agid = activationSystem.registerGroup(agd);
    String className = "pi.PiImpl";
    String location = "file:/e:/school/h7pnr1/examples/Activation-examples/pi/Server/";
    ActivationDesc ad = new ActivationDesc(agid, className, location, null);
    Remote pi = Activatable.register(ad);

    if (persistentRMIregistry)
    {
      Naming.rebind("Pi", pi);
      System.out.println("rebind in rmiregistry");
    }
    else
    {
      String ref = "E:\\school\\h7pnr1\\examples\\Activation-examples\\pi\\Client\\PiRef";
      FileOutputStream fos = new FileOutputStream(ref);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(pi);
      oos.close();
      System.out.println("reference stored on disk");
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
}
