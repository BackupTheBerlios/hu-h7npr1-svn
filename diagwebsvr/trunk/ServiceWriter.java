/******************************************************************
 * CODE FILE   : ServiceWriter.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 03-10-2005
 * Beschrijving: Multithreaded Server - ServiceWriter class
 */

import java.io.*;
import java.net.*;
import javax.activation.FileTypeMap;

public class ServiceWriter
{
	private int port;

	private OutputStream os;
	//private PrintWriter pw;
	private File bestand;
	private String lastModified;
	private long length;

	private int httpMessageINT;
	private String httpMessage;
	private int requestNumber;

	private boolean debug;

	/** Creates a new instance of ServiceWriter */

	public ServiceWriter(OutputStream osc, int port, int r, boolean debug)
	throws IOException
	{
		this.debug = debug;
		this.port = port;
		os = osc;
		//pw = new PrintWriter(os);
		requestNumber = r;
	}

	public ServiceWriter(OutputStream osc, int port, int r)
	throws IOException
	{
		this(osc, port,r, false);
	}

	public void setStatus(int ok, String message)
	{
		httpMessageINT = ok;
		httpMessage = message;
	}

	public boolean setFile (String filename)
	{
		System.out.println("" + requestNumber + ": " + "setFile( " + filename + " )");
		bestand = new File(filename);
		lastModified = WebDate.getDateFromLong(bestand.lastModified());
		length = bestand.length();
		return (bestand.canRead() && bestand.isFile());
	}

	public boolean checkModifiedSince(String modifiedSince)
	{
		if (lastModified.equals(modifiedSince) == true)
			return false;
		return true;
	}

	public void outputStatus (int ok, String message)
	throws IOException 
	{
		out("HTTP/1.1 " + ok + " " +  message);      
		System.out.println("" + requestNumber + ": " + "HTTP/1.1 " + ok + " " +  message);
	}

	public void out(String s)
	throws IOException
	{ 
		s += "\n";

		for (int i = 0; i < s.length(); i++)
			os.write((byte) s.charAt(i));

		if (debug)
			System.out.print("out: " + s);
	}

	public void out()
	throws IOException
	{
		/* Output file loaded with setFile */
		if (httpMessageINT < 0)
			httpMessageINT = 200;
		if (httpMessage == null)
			httpMessage = "OK";

		String contentType = FileTypeMap.getDefaultFileTypeMap().getContentType(bestand);
		System.out.println("" + requestNumber + ": " + "contentType: " + contentType);

		out("HTTP/1.1 " + httpMessageINT + " " +  httpMessage);
		out("Server:" + InetAddress.getLocalHost().getHostName() );
		out("Date:" + WebDate.getCurrent());
		out("Content-Type:" + contentType);
		out("Content-Length:" + length);
		out("Last-Modified:" + lastModified);

		FileInputStream fis = new FileInputStream(bestand);

		int tmp;
		while (true)
		{
			tmp = fis.read();
			if (tmp == -1) 
				break;
			os.write((byte) tmp);
		}

		System.out.println("" + requestNumber + ": " + "Finished uploading file.");
	}

	public void close()
	throws IOException
	{
		os.close();
	}
}
