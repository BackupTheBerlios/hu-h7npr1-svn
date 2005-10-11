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
	private String seperator;
	private int requestnumber;

	public Service(Socket newSocket, String path, int r)
	{
		requestnumber = r;            
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
			sr = new ServiceReader(socket.getInputStream(), socket.getLocalPort(), requestnumber);
			sw = new ServiceWriter(socket.getOutputStream(), socket.getLocalPort(), requestnumber);

			socket.setKeepAlive(true);
			while (socket.getKeepAlive() && socket.isBound() && socket.isConnected() && !socket.isClosed())
			{
				try
				{
					if (sr.readRequest())
					{
						//  Afhandeling output    
						readFails = 0;
						/* method check */
						if (sr.method.equalsIgnoreCase("GET"))
						{}
						else if (sr.method.equalsIgnoreCase("POST"))
							sr.requestUrl = "/errordocs/notsupported.html";
						else
							sr.requestUrl = "/errordocs/notsupported.html";

						/* requestUrl */
						//os dependend seperator
						//seperator = System.getProperty("file.seperator");
						seperator = File.separator;

						//replace \ met de os dependend seperator indien nodig (c.q equals aan /)
						sr.requestUrl = sr.requestUrl.replace("\\".charAt(0),seperator.charAt(0));

						boolean continueOutput;

						if (sr.requestUrl.substring(0,1).equals(seperator) && path.substring(path.length() - 1).equals(seperator))
						{
							// filename should not containt double
							continueOutput = sw.setFile(path + sr.requestUrl.substring(1));
						}
						else
							continueOutput = sw.setFile(path + sr.requestUrl);

						if (continueOutput)
						{
							// connection
							setKeepAlive(sr.connection, sr.keepAlive);

							// acceptEncoding

							// accept

							// ifModifiedSince

							// String[] userAgent

							sw.out();
						}
					}
					else
					{
						// wat hier dan
					}
				}

				catch (Exception ee)
				{
					if (ee.getMessage().equals("Connection reset"))
						break;

					if (ee.getMessage().equals("Read timed out") || ee.getMessage().equals("eof(1)") || ee.getMessage().equals("eof(2)"))
					{
						readFails++;
						if (readFails > 2)
							break;
					}
					//else
					//{
						writeDebug(ee.getMessage());
					//}
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
				ee.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public void writeDebug (String debug)
	{
		System.out.println("" + requestnumber + ": " + debug);
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
