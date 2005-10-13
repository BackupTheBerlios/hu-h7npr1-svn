/******************************************************************
 * CODE FILE   : Service.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 13-09-2005
 * Beschrijving: Meerdraadse Server - klasse Service
 */

import java.net.Socket;
import java.io.*;
//import java.lang.*;

public class Service
implements Runnable
{
	private Socket socket = null;
	private String path;
	private String separator;
	private int requestNumber;

	public Service(Socket newSocket, String path, int r)
	{
		requestNumber = r;            
		writeDebug("Service ("+newSocket+")");
		socket = newSocket;
		this.path = path;
	}

	public void run()
	{
		ServiceReader sr;
		ServiceWriter sw;
		int readFails = 0;

		try
		{
			sr = new ServiceReader(socket.getInputStream(), socket.getLocalPort(), requestNumber, false);
			sw = new ServiceWriter(socket.getOutputStream(), socket.getLocalPort(), requestNumber, false);

			if (sr.readRequest())
			{
				//  Afhandeling output    
				/* method check */
				if (sr.method.equalsIgnoreCase("GET"))
				{} // GET hoeft nog niets speciaals
				else
					sw.outputStatus(500, "Type not supported");

				if (sr.requestUrl.indexOf("../") != -1)
					sw.outputStatus(404, "Not Found");

				/* requestUrl */
				//os dependend separator
				//seperator = System.getProperty("file.seperator");
				separator = File.separator;

				//replace \ met de os dependend separator indien nodig (c.q equals aan /)
            if (separator.equals("/"))
            	sr.requestUrl = sr.requestUrl.replace("\\".charAt(0),separator.charAt(0));
            else
            	sr.requestUrl = sr.requestUrl.replace("/".charAt(0),separator.charAt(0));

				boolean continueOutput;

				if (sr.requestUrl.substring(0,1).equals(separator) && path.substring(path.length() - 1).equals(separator))
				{
					// filename should not containt double
					continueOutput = sw.setFile(path + sr.requestUrl.substring(1));
				}
				else
					continueOutput = sw.setFile(path + sr.requestUrl);

				if (continueOutput && sw.checkModifiedSince(sr.ifModifiedSince))
				{
					// connection
					setKeepAlive(sr.connection, sr.keepAlive);
					// acceptEncoding
					// accept
					// String[] userAgent
					sw.out();
				}
			}

			if (socket.getKeepAlive())
				writeDebug("Connection closed by server. Could not read");
			else
				writeDebug("Connection closed by server. Request ended");

			sr.close();
			sw.close();
			socket.close();
		}

		catch (Exception e)
		{
			try
			{
				socket.close();
			}
			catch (Exception ee)
			{
				writeDebug(ee.getMessage());
			}
			writeDebug(e.getMessage());
		}
	}

	public void writeDebug (String debug)
	{
		System.out.println("" + requestNumber + ": " + debug);
	}

	public void setKeepAlive(String connection, int keepAlive) throws IOException
	{
		if (connection != null)
		{
			if (connection.trim().equalsIgnoreCase("keep-alive"))
			{
				if (!socket.getKeepAlive())
					socket.setKeepAlive(true);
				writeDebug("setting Keep Alive: true");
			}
			else
			{
				if (socket.getKeepAlive())
				{
					socket.setKeepAlive(false);
					writeDebug("setting Keep Alive: false");
				}
			}
		}
		else
			socket.setKeepAlive(true);

		if (keepAlive > 0)
			socket.setSoTimeout(keepAlive);
		else
			socket.setSoTimeout(600);
	}
}
