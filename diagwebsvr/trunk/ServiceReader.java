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
	Hashtable requestContents;


	
	
	
	
	public String method;
	public String requestUrl;
	public String domainName;
	public String httpVersion;

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
		requestContents = new Hashtable();
	}

	public Hashtable getRequestContents()
	{
		Hashtable requestContents = new Hashtable();
		
		
		return requestContents;
	}

	public String getRequest()
	throws IOException
	{
		/* We only support HTTP GET/HEAD, and don't
		 * support any fancy HTTP options,
		 */
		requestContents.clear();
		int totalBytesRead = 0, bytesRead = 0;

outerloop:
		while (totalBytesRead < Service.BUFFER_SIZE)
		{
			bytesRead = is.read(requestBuffer, totalBytesRead, Service.BUFFER_SIZE - totalBytesRead);
			if (bytesRead == -1)
			{
				/* EOF */
				if (Server.DEBUG_MODE) writeDebug("[DEBUG] Request Type Not Available");
				return "Request Type Not Available";
			}
			int index = totalBytesRead;
			totalBytesRead += bytesRead;
			for (; index < totalBytesRead; index++)
			{
				if (requestBuffer[index] == (byte)'\n' || requestBuffer[index] == (byte)'\r')
				{
					/* read one line */
					if (Server.DEBUG_MODE) writeDebug("[DEBUG] break outerloop");
					break outerloop;
				}
			}
		}

		/* beginning of file name */
		int fileNameStartIndex = 0;
		if (requestBuffer[0] == (byte)'G' &&
				requestBuffer[1] == (byte)'E' &&
				requestBuffer[2] == (byte)'T' &&
				requestBuffer[3] == (byte)' ')
		{
			requestContents.put("requestType", "GET");
			fileNameStartIndex = 4;
		}
		else if (requestBuffer[0] == (byte)'H' &&
				requestBuffer[1] == (byte)'E' &&
				requestBuffer[2] == (byte)'A' &&
				requestBuffer[3] == (byte)'D' &&
				requestBuffer[4] == (byte)' ')
		{
			requestContents.put("requestType", "HEAD");
			fileNameStartIndex = 5;
		}
		else
		{
			/* We don't support this method */
			requestContents.put("requestType", new String(requestBuffer).substring(0, 5));
			if (Server.DEBUG_MODE) writeDebug("[DEBUG] requestType == " + requestContents.get("requestType"));
			return (String)requestContents.get("requestType");
		}
		if (Server.DEBUG_MODE) writeDebug("[DEBUG] requestType == " + requestContents.get("requestType"));

		int i = 0;
		/* find the file name, from:
		 * GET /foo/bar.html HTTP/1.0
		 * extract "/foo/bar.html"
		 */
		for (i = fileNameStartIndex; i < totalBytesRead; i++)
		{
			if (requestBuffer[i] == (byte)' ')
			{
				break;
			}
		}

		/* Replace all separator characters with
		 * the OS specific type
		 * (replacing '/' with '\' if necessary)
		 */
		String fileName = (new String(requestBuffer, fileNameStartIndex, i-fileNameStartIndex)).replace('/', File.separatorChar);
		if (fileName.startsWith(File.separator))
			fileName = fileName.substring(1);
		requestContents.put("fileName", fileName);

		return (String)requestContents.get("requestType");
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
			if (x == 1)
				setRequestLine(newLine);
			else if (!dataReceiving)
				dataReceiving = setRequestHeader(newLine);
			else
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

	public void setRequestLine(String s)
	{
		StringTokenizer st = new StringTokenizer(s);
		if (st.countTokens() != 3)
			System.out.println("" + requestNumber + ": " + "RequestLine is niet volgens HTTP Protocol");

		method = st.nextToken();
		requestUrl = st.nextToken();

		if (requestUrl.indexOf("http://") != -1)
		{
			StringTokenizer st2 = new StringTokenizer(requestUrl);
			st2.nextToken("//");
         domainName = requestUrl = st2.nextToken("/");
         requestUrl = "/" + st2.nextToken("");
		}

		if (requestUrl.equals("*"))
			requestUrl = "/index.html"; /* no particular resource */
		else if (requestUrl.equals("/"))
			requestUrl = "/index.html"; /* request index.html */

		httpVersion = st.nextToken();
		if (Server.DEBUG_MODE)
		{
			System.out.println("" + requestNumber + ": " + "Methode: " + method);
			System.out.println("" + requestNumber + ": " + "Domainname: " + domainName);
			System.out.println("" + requestNumber + ": " + "requestUrl: " + requestUrl);
			System.out.println("" + requestNumber + ": " + "httpVersion: " + httpVersion);
			System.out.println("");
		}
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
			if (domainName == null && host != null)
			{
				StringTokenizer st2 = new StringTokenizer(host, ":");
				domainName = st2.nextToken();
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
