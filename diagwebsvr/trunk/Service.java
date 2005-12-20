/******************************************************************
 * CODE FILE   : Service.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 20-12-2005
 * Beschrijving: Meerdraadse Server - klasse Service
 */

import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;
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

	/* A place to store the separate Request-Line contents */
	HashMap requestMap;

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

	private void handleClient()
	throws IOException
	{
		ServiceReader sr = new ServiceReader(new BufferedInputStream(socket.getInputStream()), requestNumber);
		ServiceWriter sw = new ServiceWriter(socket.getOutputStream(), requestNumber);
		requestMap = new HashMap();

		/* we will only block in read for this many milliseconds
		 * before we fail with java.io.InterruptedIOException,
		 * at which point we will abandon the connection.
		 */
		socket.setSoTimeout(Server.timeout);
		socket.setTcpNoDelay(true);

		try
		{
			if (sr.getRequestLine() &&
					!sr.requestLine.isEmpty())
			{
				analyseRequestLine((String)sr.requestLine.firstElement());

				String requestType = (String)requestMap.get("Method");

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
				
				/* Analyse the rest of the headers (if they exist) */
				for (int index = 1; index < sr.requestLine.size(); index++)
				{
					analyseHeader((String)sr.requestLine.get(index));
				}

				File target = new File(Server.virtualRoot, (String)requestMap.get("Request-URI"));
				writeDebug("From " + socket.getInetAddress().getHostAddress() + ": " +
						requestType + " " + target.getAbsolutePath());
				/* If a directory is requested, send index.html
				 * by default (is available)
				 */
				if (target.isDirectory())
				{
					File indexFile = new File(target, "index.html");
					if (indexFile.exists())
						target = indexFile;
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
					if (OK && target.isDirectory())
						sw.sendDirectoryListing(target);
					else if (OK)
						sw.sendFile(target);
					else
						sw.send404();
			}
			else
				sw.send404();
		}
		finally
		{
			socket.close();
		}
	}

	private void analyseRequestLine(String inString)
	{
		StringTokenizer stringTokenizer = new StringTokenizer(inString);
		if (stringTokenizer.countTokens() != 3)
		{
			writeDebug("RequestLine is niet volgens HTTP Protocol");
			return;
		}

		String method = stringTokenizer.nextToken();
		String requestURI = stringTokenizer.nextToken();
		String domainName = "";

		if (requestURI.indexOf("http://") != -1)
		{
			StringTokenizer stringTokenizer2 = new StringTokenizer(requestURI);
			stringTokenizer2.nextToken("//");
         domainName = requestURI = stringTokenizer2.nextToken("/");
         requestURI = "/" + stringTokenizer2.nextToken("");
		}

		if (requestURI.equals("*"))
			requestURI = "/index.html"; /* no particular resource */
		else if (requestURI.equals("/"))
			requestURI = "/index.html"; /* request index.html */

		String httpVersion = stringTokenizer.nextToken();

		requestMap.put("Method", method);
		requestMap.put("Request-URI", requestURI);
		requestMap.put("Domain-Name", domainName);
		requestMap.put("HTTP-Version", httpVersion);

		if (Server.DEBUG_MODE)
		{
			writeDebug("Method: " + method);
			writeDebug("Domain-Name: " + domainName);
			writeDebug("Request-URI: " + requestURI);
			writeDebug("HTTP-Version: " + httpVersion);
		}
	}

	private void analyseHeader(String inString)
	{
		StringTokenizer stringTokenizer = new StringTokenizer(inString, ":");
		String command = stringTokenizer.nextToken().trim();
		String data = "";

		/* Store command and data in the requestMap
		 * but only if the command exists in the
		 * Server.commandList
		 */
		if (Server.commandList.contains(command))
		{
			data = stringTokenizer.nextToken().trim();
			requestMap.put(command, data);
			if (Server.DEBUG_MODE) writeDebug("analyseHeader(): " + command + " == " + data);
		}
	}

	private void writeDebug(String debug)
	{
		System.out.println("" + requestNumber + ": " + debug);
	}
}
