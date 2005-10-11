/******************************************************************
 * CODE FILE   : Server.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 13-09-2005
 * Beschrijving: Multithreaded Server - Server class
 */

import java.net.ServerSocket;
import java.net.Socket;

class Server
implements Runnable
{
	private boolean exit;
	private int port;
	private int backLog;
	private ServerSocket serverSocket;
	private String path;

	public Server(int port, int backLog, String path)
	{
		this.port = port;
		this.backLog = backLog;
		this.path = path;
	}

	public void run()
	{
		try
		{
			this.exit = false;
			writeDebug("De server is gestart.");
			serverSocket = new ServerSocket(port, backLog);
			int x = 0;
			while(!this.exit)
			{
				x++;
				Socket socket = serverSocket.accept();
				Service service = new Service(socket, path, x);
				Thread thread = new Thread(service);
				thread.start();
			}
			writeDebug("De server is gestopt.");
		}
		catch (Exception e)
		{
			writeDebug(e);
		}
	}

	public void stop()
	{
		this.exit = true;
		try
		{
			serverSocket.close();
			writeDebug("Socket Closed");
		}
		catch (Exception e)
		{
			writeDebug(e);
		}
	}

	public void writeDebug (Object debug)
	{
		System.out.println(debug);
		System.out.println("");
	}
}
