/******************************************************************
 * CODE FILE   : ServiceReader.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-12-2005
 * Beschrijving: Meerdraadse Server - klasse ServiceReader
 */

import java.io.*;
//import java.net.SocketException;
import java.util.*;

public class ServiceReader
//extends FilterInputStream
{
	private InputStream is;
	private int requestNumber;

	/* buffer to use for requests */
	byte[] requestBuffer;
	
	/* A place to store the header contents */
	HashMap requestMap;


	
	
	
	
	private HashMap requests;

	private Vector input;

	public String dateRequest;
	public String connection;
	public int keepAlive;
	public HashSet acceptEncoding;
	public HashSet accept;
	public String acceptLanguage;
	public String ifModifiedSince;
	public String host;
	public String userAgent;

	public String contentLength;
	public String contentType;

	/** Creates a new instance of ServiceReader */
	public ServiceReader(InputStream is, int requestNumber)
	throws IOException
	{
		this.is = is;
		this.requestNumber = requestNumber;
		requestBuffer = new byte[Service.BUFFER_SIZE];
		requestMap = new HashMap();
	}

	public String getRequestLines()
	throws IOException
	{
		String previousLine = "", currentLine = "";
		requestMap.clear();

		currentLine = readLine();
		if (Server.DEBUG_MODE) writeDebug("Request-Line: " + currentLine);
		analyseRequestLine(currentLine);
		String method = (String)requestMap.get("Method");

		if (!method.equals("GET"))
		{
			/* We don't support this method */
			return method;
		}

		/** retrieve the rest of the request data **/

		return method;
	}

	private String readLine()
	throws IOException
	{
		int totalBytesRead = 0;
		byte[] tempRequestBuffer = new byte[1];

		/* zero out the request buffer from last time */
		for (int index = 0; index < Service.BUFFER_SIZE; index++)
			requestBuffer[index] = 0;

		if (is.read(tempRequestBuffer, 0, 1) == -1)
				throw new IOException("eof(1)");

		requestBuffer[totalBytesRead] = tempRequestBuffer[0];
		totalBytesRead++;

		if (is.read(tempRequestBuffer, 0, 1) == -1)
			throw new IOException("eof(2)");

		while (totalBytesRead < Service.BUFFER_SIZE &&
				requestBuffer[totalBytesRead-1] != (byte)'\r' &&
				(requestBuffer[totalBytesRead] = tempRequestBuffer[0]) != (byte)'\n' &&
				is.read(tempRequestBuffer, 0, 1) != -1)
		{
			totalBytesRead++;
		}

		return new String(requestBuffer).substring(0, totalBytesRead-1);
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

	private void writeDebug(String debugText)
	{
		System.out.println("" + requestNumber + ": " + debugText);
	}

	/**************************************************/
	/********** A LOTTA OLD STUFF AFTER THIS **********/
	/**************************************************/

	public boolean readRequest_old()
	throws IOException
	{
		input = new Vector();
		accept = new HashSet();
		acceptEncoding = new HashSet();
		requests = new HashMap();

		int x = 0;
		String newLine = "";
		boolean dataReceiving = false;

		while (true)
		{
//			newLine = readLine();
			if (newLine == null)
				break;
			x++;
//			if (x == 1)
//				setRequestLine(newLine);
//			else if (!dataReceiving)
//				dataReceiving = setRequestHeader(newLine);
//			else
//				setData(newLine);
			if (Server.DEBUG_MODE)
				input.add(""  + x + ": " + newLine);
		}

		if (x == 0)
			return false;
		
		handleRequestsHeaders();

		if (Server.DEBUG_MODE)
		{
			if (dateRequest != null)
				System.out.println("" + requestNumber + ": " + "dateRequest: " + dateRequest);
			if (connection != null)
				System.out.println("" + requestNumber + ": " + "connection: " + connection);

			if (acceptLanguage != null) System.out.println("" + requestNumber + ": " + "acceptLanguage: " + acceptLanguage);
			if (ifModifiedSince != null) System.out.println("" + requestNumber + ": " + "ifModifiedSince: " + ifModifiedSince);
			if (host != null) System.out.println("" + requestNumber + ": " + "host: " + host);
			if (contentType != null) System.out.println("" + requestNumber + ": " + "contentType: " + contentType);
			if (contentLength != null) System.out.println("" + requestNumber + ": " + "contentLength: " + contentLength);

			System.out.println("" + requestNumber + ": " + "userAgent: " + userAgent);
			System.out.println("" + requestNumber + ": " + "");
			System.out.println("" + requestNumber + ": " + "HTTP MESSAGE: ");
			for (Enumeration e = input.elements() ; e.hasMoreElements() ;)
				System.out.println("" + requestNumber + ":   " + e.nextElement());
			System.out.println("");
		}

		return true;
	}

	public boolean setRequestHeader(String s)
	{
		StringTokenizer st = new StringTokenizer(s, ":");
		String rh = st.nextToken();

		if (!Server.commandList.contains(rh))
		{
//			setData(s);
			return true;
		}

		requests.put(rh, st.nextToken("blablbalbalablabla"));
		if (Server.DEBUG_MODE)
			System.out.println("setRequestHeader: " + rh );

		return false;
	}

	public void handleRequestsHeaders()
	{
		/*
		 *
		 *   Accept-Encoding:gzip,deflate
		 *   Accept: sterretje/sterretje
		 *   Accept-Language:nl
		 *   If-None-Match:�0851736ca12c21:8ef�
		 *   Host:mycom
		 *                     
		 */

		connection = (String) requests.get("Connection");
		acceptLanguage = (String) requests.get("Accept-Language");

      /*
       *   User-Agent:mozilla/4.0( compatible
       *                         ; msie 6.0
       *                         ; windows nt 5.1
       *                         ; q312461
       *                         ; .net clr 1.0.3705
       *                         )            
       */

		userAgent = (String) requests.get("User-Agent");
		/* MUST */
		contentLength = (String) requests.get("Content-Length");        
		/* MUST */
		contentType = (String) requests.get("Content-Type");
      
		String request;

		if ((request = (String) requests.get("Date")) != null)
		{
			StringTokenizer st = new StringTokenizer(request);
			dateRequest = st.nextToken(">");
		}

		if ((request = (String) requests.get("Accept")) != null)
		{
			StringTokenizer st = new StringTokenizer(request);
			String ac = st.nextToken(",");
			accept.add(ac);
			while (st.hasMoreTokens())
			{
				ac = st.nextToken();
				accept.add(ac);
			}
		}

		if ((request = (String) requests.get("Accept-Encoding")) != null)
		{
			StringTokenizer st = new StringTokenizer(request);
			String ac = st.nextToken(",");
			acceptEncoding.add(ac);
			while (st.hasMoreTokens())
			{
				ac = st.nextToken();
				acceptEncoding.add(ac);
			}
		}

		if ((request = (String) requests.get("Host")) != null)
		{
			String host = request;
//			if (domainName == null && host != null)
			{
				StringTokenizer st2 = new StringTokenizer(host, ":");
//				domainName = st2.nextToken();
			}
		}

		if ((request = (String) requests.get("If-Modified-Since")) != null)
		{
			//If-Modified-Since:thu, 13 jun 2002 11:05:22 gmt
			StringTokenizer st = new StringTokenizer(request);
			ifModifiedSince = st.nextToken(">");
		}

		if ((request = (String) requests.get("Keep-Alive")) != null)
		{
			try
			{
				keepAlive = Integer.parseInt(request);
			}
			catch (NumberFormatException e)
			{
				System.out.println("" + requestNumber + ": " + "Keep-Alive kan niet gezet worden in setRequestHeader");
			}
		}
	}
}
