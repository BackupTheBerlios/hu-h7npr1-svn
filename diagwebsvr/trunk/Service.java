/******************************************************************
 * CODE FILE   : Service.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-12-2005
 * Beschrijving: Meerdraadse Server - klasse Service
 */

import java.net.Socket;
import java.io.*;

public class Service
implements HttpConstants, Runnable
{
	static final int BUFFER_SIZE = 1024;
	static final byte[] EOL = {(byte)'\r', (byte)'\n' };

	private Socket socket = null;
	private String codeBase;
	private String separator;
	private int requestNumber;

   public Service(Socket newSocket, int requestNumber)
	{
		writeDebug("Service (" + newSocket + ")");
		socket = newSocket;
		this.requestNumber = requestNumber;
	}

	public void run()
	{
		if (socket == null)
		{
			/* nothing to do */
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				/* should not happen */
				e.printStackTrace();
			}
		}

		try
		{
			handleClient();
		}
		catch (Exception e)
		{
			/* should not happen */
			e.printStackTrace();
		}

		socket = null;
	}

	void handleClient()
	throws IOException
	{
		ServiceReader sr = new ServiceReader(new BufferedInputStream(socket.getInputStream()), requestNumber);
		ServiceWriter sw = new ServiceWriter(socket.getOutputStream(), requestNumber);

		/* we will only block in read for this many milliseconds
		 * before we fail with java.io.InterruptedIOException,
		 * at which point we will abandon the connection.
		 */
		socket.setSoTimeout(Server.timeout);
		socket.setTcpNoDelay(true);

		try
		{
			String requestType = sr.getRequest();
			int index = 0;
			/* are we doing a GET or a HEAD */
			if (!requestType.equals("GET") && !requestType.equals("HEAD"))
			{
				/* We don't support this method
				 * Sending code 405 as a response
				 */
				sw.send405(requestType);
				socket.close();
				return;
			}

			File target = new File(Server.virtualRoot, (String)sr.requestContents.get("fileName"));
			writeDebug("From " + socket.getInetAddress().getHostAddress() + ": " +
					requestType + " " + target.getAbsolutePath());
			/* If a directory is requested, send index.html
			 * by default (is available)
			 */
			if (target.isDirectory())
			{
				File indexFile = new File(target, "index.html");
				if (indexFile.exists())
				{
					target = indexFile;
				}
			}
			/* Sending headers,
			 * returning 'true' if operation succeeded
			 */
			boolean OK = sw.sendHeaders(target);
			/* Response to GET request,
			 * sending directory listing,
			 * file or code 404 (not found)
			 */
			if (requestType.equals("GET"))
			{
				if (OK && target.isDirectory())
				{
					sw.sendDirectoryListing(target);
				}
				else if (OK)
				{
					sw.sendFile(target);
				}
				else
				{
					sw.send404();
				}
			}
		}
		finally
		{
			socket.close();
		}
	}

	private void writeDebug(String debug)
	{
		System.out.println("" + requestNumber + ": " + debug);
	}
}
