/******************************************************************
 * CODE FILE   : ServiceReader.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 20-12-2005
 * Beschrijving: Meerdraadse Server - klasse ServiceReader
 */

import java.io.*;
import java.util.*;

public class ServiceReader
//extends FilterInputStream
{
	private InputStream is;
	private int requestNumber;

	/* A place to store the Request-Line */
	Vector requestLine;

	/** Creates a new instance of ServiceReader */
	public ServiceReader(InputStream is, int requestNumber)
	throws IOException
	{
		this.is = is;
		this.requestNumber = requestNumber;
		requestLine = new Vector();
	}

	public boolean getRequestLine()
	{
		String previousLine = "", currentLine = "";
		requestLine.clear();

		try
		{
			while (!(currentLine = readLine()).equals("\r\n") &&
					!previousLine.equals("\r\n"))
			{
				if (!currentLine.equals("\r\n") &&
						!currentLine.equals(""))
				{
					requestLine.addElement(currentLine);
				}
				previousLine = currentLine;
			}
			return true;
		}
		catch (IOException e)
		{
			writeDebug("" + e);
			return false;
		}
	}

	private String readLine()
	throws IOException
	{
		int totalBytesRead = 0;
		/* buffer to use for requests */
		byte[] requestBuffer = new byte[Service.BUFFER_SIZE];

		if (is.read(requestBuffer, totalBytesRead++, 1) == -1)
			throw new IOException("eof(1)");
		if (is.read(requestBuffer, totalBytesRead++, 1) == -1)
			throw new IOException("eof(2)");
		/* Return when this is it... */
		if (requestBuffer[totalBytesRead-2] == (byte)'\r' &&
				requestBuffer[totalBytesRead-1] == (byte)'\n')
			return new String(requestBuffer).substring(0, totalBytesRead);

		/* When still not reached EOF, continue
		 * reading the rest of the bytes as long as...
		 */
		while (totalBytesRead < Service.BUFFER_SIZE &&
				is.read(requestBuffer, totalBytesRead, 1) != -1 &&
				!(requestBuffer[totalBytesRead++] == (byte)'\n' &&
						requestBuffer[totalBytesRead-2] == (byte)'\r'))
		{}

		/* Trim \r and \n */
		while (totalBytesRead > 0 &&
				(requestBuffer[totalBytesRead-1] == (byte)'\r' ||
						requestBuffer[totalBytesRead-1] == (byte)'\n'))
			totalBytesRead--;

		String retVal = new String(requestBuffer).substring(0, totalBytesRead);
		if (Server.DEBUG_MODE) writeDebug ("ServiceReader.readLine(): " + retVal);
		return retVal;
	}

	private void writeDebug(String debugText)
	{
		System.out.println("" + requestNumber + ": " + debugText);
	}
}
