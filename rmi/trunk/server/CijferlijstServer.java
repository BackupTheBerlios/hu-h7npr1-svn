/******************************************************************
 * CODE FILE   : CijferlijstServer.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 18-01-2006
 * Beschrijving: Class CijferlijstServer - Instantiating Cijfer-
 *               lijstServiceImpl and registering it with the
 *               RMIRegistry
 */
package server;

import interfaces.CijferlijstService;
import java.rmi.Naming;

public class CijferlijstServer
{
	public static void main(String[] args)
	throws Exception
	{
		String codebase = "http://10.19.133.7:4711/";
		System.setProperty("java.rmi.server.codebase", codebase);
		System.setProperty("java.rmi.server.logCalls", "true"  );
		System.setProperty("java.rmi.dgc.leaseValue", "600000"); // 10 minutes

		// choose SILENT, BRIEF or VERBOSE
		System.setProperty("sun.rmi.dgc.logLevel",             "SILENT");
		System.setProperty("sun.rmi.loader.logLevel",          "SILENT");
		System.setProperty("sun.rmi.server.logLevel",          "SILENT");
		System.setProperty("sun.rmi.transport.logLevel",       "SILENT");
		System.setProperty("sun.rmi.transport.proxy.logLevel", "SILENT");
		System.setProperty("sun.rmi.transport.tcp.logLevel",   "SILENT");

		// Instantiating CijferlijstServiceImpl)
		CijferlijstServiceImpl cijferlijst = new CijferlijstServiceImpl();
		// Register the service
		Naming.rebind("rmi://10.19.133.7:1099/"+CijferlijstService.refName, cijferlijst);
		System.out.println("Codebase="+codebase);
		System.out.println("Reference="+cijferlijst);
		System.out.println("Server ready...");
	}
}
