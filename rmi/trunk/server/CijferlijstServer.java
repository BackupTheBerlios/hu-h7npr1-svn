/*
 * Created on Jan 18, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package server;

import interfaces.CijferlijstService;
import java.rmi.Naming;

/**
 * @author ebeukhof
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CijferlijstServer
{
	public static void main(String[] args)
	throws Exception
	{
		String codebase = "http://192.168.0.93:4711/";
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

//		if (System.getSecurityManager() == null)
//			System.setSecurityManager(new RMISecurityManager());

		CijferlijstServiceImpl cijferlijst = new CijferlijstServiceImpl();
		Naming.rebind("rmi://192.168.0.93:1099/"+CijferlijstService.refName, cijferlijst);
		System.out.println("Codebase="+codebase);
		System.out.println("Reference="+cijferlijst);
		System.out.println("Server ready...");
	}
}
