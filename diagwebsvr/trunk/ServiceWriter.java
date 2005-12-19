/******************************************************************
 * CODE FILE   : ServiceWriter.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-12-2005
 * Beschrijving: Multithreaded Server - ServiceWriter class
 */

import java.io.*;
//import java.net.*;
import java.util.Date;

public class ServiceWriter
implements HttpConstants
{
   static final byte[] EOL = {(byte)'\r', (byte)'\n' };

	private PrintStream ps;
	private int requestNumber;
	private boolean debugMode;

	/* buffer to use for requests */
	byte[] requestBuffer;

	/** Creates a new instance of ServiceWriter */
	public ServiceWriter(OutputStream newOS, int requestNumber, boolean debugMode)
	throws IOException
	{
		ps = new PrintStream(newOS);
		this.requestNumber = requestNumber;
		this.debugMode = debugMode;
		requestBuffer = new byte[Service.BUFFER_SIZE];
	}

	public ServiceWriter(OutputStream newOS, int requestNumber)
	throws IOException
	{
		this(newOS, requestNumber, false);
	}

	boolean sendHeaders(File target)
	throws IOException
	{
		boolean targetExists = target.exists();
		int responseCode = 0;
		String responseText = "";
		/****************/
		/*** HTTP/x.y ***/
		/****************/
		if (!targetExists)
		{
			responseCode = HTTP_NOT_FOUND;
			responseText = "HTTP/1.1 " + HTTP_NOT_FOUND + " not found";
			ps.print(responseText);
			ps.write(EOL);
		}
		else
		{
			responseCode = HTTP_OK;
			responseText = "HTTP/1.1 " + HTTP_OK+" OK";
			ps.print(responseText);
			ps.write(EOL);
		}
		writeDebug("Sent: " + responseText);
		/****************/
		/**** Server ****/
		/****************/
		ps.print("Server: Diagnostic WebServer");
		ps.write(EOL);
		/****************/
		/***** Date *****/
		/****************/
		ps.print("Date: " + WebDate.getCurrent());
		ps.write(EOL);
		/********************/
		/** Content-length **/
		/** Last Modified ***/
		/*** Content-type ***/
		/********************/
		if (targetExists)
		{
			if (!target.isDirectory())
			{
				ps.print("Content-length: "+target.length());
				ps.write(EOL);
				ps.print("Last Modified: " + (new Date(target.lastModified())));
				ps.write(EOL);
				String name = target.getName();
				int index = name.lastIndexOf('.');
				String contentType = null;
				if (index > 0)
				{
					contentType = (String) Server.contentTypeMap.get(name.substring(index));
				}
				if (contentType == null)
				{
					contentType = "unknown/unknown";
				}
				responseText = "Content-type: " + contentType;
				ps.print(responseText);
				ps.write(EOL);
			}
			else
			{
				responseText = "Content-type: text/html";
				ps.print(responseText);
				ps.write(EOL);
			}
			writeDebug("Sent: " + responseText);
		}
		return targetExists;
	}

	void send404()
	throws IOException
	{
		ps.write(EOL);
		ps.write(EOL);
		ps.println("Not Found\n\nThe requested resource was not found.\n");
	}

	void send405(String methodType)
	throws IOException
	{
		String responseText = "HTTP/1.0 " + HTTP_BAD_METHOD + 
			" unsupported method type: " + methodType;
		ps.print(responseText);
		ps.write(EOL);
		ps.flush();
		writeDebug("Sent: " + responseText);
	}

	void sendFile(File target)
	throws IOException
	{
		String targetAbsolutePath = target.getAbsolutePath();
		writeDebug("About to send file: " + targetAbsolutePath);
		ps.write(EOL);
		InputStream is = new FileInputStream(targetAbsolutePath);
		try
		{
			int byteCount;
			while ((byteCount = is.read(requestBuffer)) > 0)
			{
				ps.write(requestBuffer, 0, byteCount);
			}
			writeDebug("Finished uploading file.");
		}
		finally
		{
			is.close();
		}
	}

	void sendDirectoryListing(File target)
	throws IOException
	{
		ps.write(EOL);
		ps.println("<TITLE>Directory listing</TITLE><P>\n");
		ps.println("<A HREF=\"..\">Parent Directory</A><BR>\n");
		String[] list = target.list();
		for (int i = 0; list != null && i < list.length; i++)
		{
			File f = new File(target, list[i]);
			if (f.isDirectory())
			{
				ps.println("<A HREF=\""+list[i]+"/\">"+list[i]+"/</A><BR>");
			}
			else
			{
				ps.println("<A HREF=\""+list[i]+"\">"+list[i]+"</A><BR");
			}
		}
		ps.println("<P><HR><BR><I>" + (new Date()) + "</I>");
		writeDebug("Finished sending directory listing.");
	}

	private void writeDebug(String debugText)
	{
		System.out.println("" + requestNumber + ": " + debugText);
	}
}
