/******************************************************************
 * CODE FILE   : ServiceReader.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-09-2005
 * Beschrijving: Meerdraadse Server - klasse ServiceReader
 */

import java.io.*;
//import java.net.SocketException;
import java.util.*;

public class ServiceReader 
extends FilterInputStream
{
	private int port;
	private int requestNumber;

	public String method;
	public String requestUrl;
	public String domainName;
	public String httpVersion;

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

	private boolean debug = false;

	/** Creates a new instance of ServiceReader */
	public ServiceReader(InputStream is, int port, int r, boolean debug)
	throws IOException
	{
		super(is); 
		this.debug = debug;
		this.port = port;
		requestNumber = r;
    }
    
	public ServiceReader(InputStream is, int port, int r)
	throws IOException
	{
		this(is, port, r, false);
	}

	public boolean readRequest()
	throws IOException
	{  
		input = new Vector();
		accept = new HashSet();
		acceptEncoding = new HashSet();

		int x = 0;
		String newline = "";
		boolean datareceiving = false;

		while (true)
		{ //available();
			newline = readLine();
			if (newline == null)
				break;
			x++;
			if (x == 1)
				setRequestLine(newline);
			else if (!datareceiving)
				datareceiving = setRequestHeader(newline);
			else
				setData(newline);
			if (debug)
				input.add(""  + x + ": " + newline);
			x++;
		}

		if (x == 0)
			return false;

		if (debug)
		{
			if (dateRequest != null)
				System.out.println("" + requestNumber + ": " + "dateRequest: " + dateRequest);
			if (connection != null)
				System.out.println("" + requestNumber + ": " + "connection: " + connection);

			//HashSet acceptEncoding;
			//HashSet accept;

			if (acceptLanguage != null) System.out.println("" + requestNumber + ": " + "acceptLanguage: " + acceptLanguage);
			if (ifModifiedSince != null) System.out.println("" + requestNumber + ": " + "ifModifiedSince: " + ifModifiedSince);
			if (host != null) System.out.println("" + requestNumber + ": " + "host: " + host);
			if (contentType != null) System.out.println("" + requestNumber + ": " + "contentType: " + contentType);
			if (contentLength != null) System.out.println("" + requestNumber + ": " + "contentLength: " + contentLength);

			System.out.println("" + requestNumber + ": " + "userAgent: " + userAgent);
			System.out.println("" + requestNumber + ": " + "");
			System.out.println("" + requestNumber + ": " + "HTTP MESSAGE: ");
			for (Enumeration e = input.elements() ; e.hasMoreElements() ;)
			{
				System.out.println(e.nextElement());
			}
			System.out.println("");
		}

		return true;
	}

	public void setRequestLine(String s)
	{
		StringTokenizer st = new StringTokenizer(s);
		if (st.countTokens() != 3)
		{
			System.out.println("" + requestNumber + ": " + "RequestLine is niet volgens HTTP Protocol");
		}

		method = st.nextToken();
		requestUrl = st.nextToken();
		if (requestUrl.equals("*"))
			requestUrl = "/index.html"; /* no particular resource */
		else if (requestUrl.equals("/"))
			requestUrl = "/index.html"; /* request index.html */
		else if (requestUrl.indexOf("../") != -1)
			requestUrl = "/errordocs/404.html"; /* request index.html */
		else if (requestUrl.indexOf("http://") != -1)
		{
			StringTokenizer st2 = new StringTokenizer(requestUrl);
			st2.nextToken("//");
			domainName = requestUrl = st2.nextToken("/");
			requestUrl = "/" + st2.nextToken("");
		}

		httpVersion = st.nextToken();
		if (debug)
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

		if (debug)
			System.out.println("setRequestHeader: " + rh );

		/*
		 *
		 *   Accept-Encoding:gzip,deflate
		 *   Accept: sterretje/sterretje
		 *   Accept-Language:nl
		 *   If-None-Match:�0851736ca12c21:8ef�
		 *   Host:mycom
		 *                     
		 */

		if (rh.equalsIgnoreCase("Cache-Control"))
		{}
		else if (rh.equalsIgnoreCase("Connection"))
			connection = st.nextToken();
		else if (rh.equalsIgnoreCase("Date"))
			dateRequest = st.nextToken(">");    
		else if (rh.equalsIgnoreCase("Pragma"))
		{}
		else if (rh.equalsIgnoreCase("Trailer"))
		{}
		else if (rh.equalsIgnoreCase("Transer-Encoding"))
		{}
		else if (rh.equalsIgnoreCase("Upgrade"))
		{}
		else if (rh.equalsIgnoreCase("Via"))
		{}
		else if (rh.equalsIgnoreCase("Warning"))
		{}
		else if (rh.equalsIgnoreCase("Accept"))
		{
			String ac = st.nextToken(",");
			accept.add(ac);
			while (st.hasMoreTokens())
			{
				ac = st.nextToken();
				accept.add(ac);
			}            
		}
		else if (rh.equalsIgnoreCase("Accept-Charset"))
		{}
		else if (rh.equalsIgnoreCase("Accept-Encoding"))
		{
			String ac = st.nextToken(",");
			acceptEncoding.add(ac);
			while (st.hasMoreTokens())
			{
				ac = st.nextToken();
				acceptEncoding.add(ac);
			}
		}
		else if (rh.equalsIgnoreCase("Accept-Language"))
		{
			acceptLanguage = st.nextToken();
		}
		else if (rh.equalsIgnoreCase("Authorization"))
		{}
		else if (rh.equalsIgnoreCase("Expect"))
		{}
		else if (rh.equalsIgnoreCase("From"))
		{}
		else if (rh.equalsIgnoreCase("Host"))
		{
			String host = st.nextToken();

			if (domainName == null && host != null)
			{
				StringTokenizer st2 = new StringTokenizer(host, ":");
				domainName = st2.nextToken();
			}
		}
		else if (rh.equalsIgnoreCase("If-Match"))
		{}
		else if (rh.equalsIgnoreCase("If-Modified-Since"))
		{
			//If-Modified-Since:thu, 13 jun 2002 11:05:22 gmt
			ifModifiedSince = st.nextToken(">");
		}
		else if (rh.equalsIgnoreCase("If-None-Match"))
		{}
		else if (rh.equalsIgnoreCase("If-Range"))
		{}
		else if (rh.equalsIgnoreCase("If-Unmodified-Since"))
		{}
		else if (rh.equalsIgnoreCase("Max-Forwards"))
		{}
		else if (rh.equalsIgnoreCase("Proxy-Authorization"))
		{}
		else if (rh.equalsIgnoreCase("Proxy-Authorization"))
		{}
		else if (rh.equalsIgnoreCase("Referer"))
		{}
		else if (rh.equalsIgnoreCase("TE"))
		{}
		else if (rh.equalsIgnoreCase("User-Agent"))
		{
			/*
			 *      User-Agent:mozilla/4.0( compatible
			 *                            ; msie 6.0
			 *                            ; windows nt 5.1
			 *                            ; q312461
			 *                            ; .net clr 1.0.3705
			 *                            )            
			 */
			userAgent = st.nextToken();
		}
		else if (rh.equalsIgnoreCase("Allow"))
		{}
		else if (rh.equalsIgnoreCase("Content-Encoding"))
		{}
		else if (rh.equalsIgnoreCase("Content-Language"))
		{}
		else if (rh.equalsIgnoreCase("Content-Length"))
		{
			/* MUST */
			contentLength = st.nextToken();
		}
		else if (rh.equalsIgnoreCase("Content-Location"))
		{}
		else if (rh.equalsIgnoreCase("Content-MD5"))
		{}
		else if (rh.equalsIgnoreCase("Content-Range"))
		{}
		else if (rh.equalsIgnoreCase("Content-Type"))
		{
			/* MUST */
			contentType = st.nextToken();
		}
		else if (rh.equalsIgnoreCase("Expires"))
		{}
		else if (rh.equalsIgnoreCase("Last-Modified"))
		{}
		else if (rh.equalsIgnoreCase("extension-header"))
		{}
		else if (rh.equalsIgnoreCase("Keep-Alive"))
		{
			try
			{
				keepAlive = Integer.parseInt(st.nextToken());
			}
			catch (NumberFormatException e)
			{
				System.out.println("" + requestNumber + ": " + "Keep-Alive kan niet gezet worden in setRequestHeader");
			}
		}
		else
		{
			setData(s);
			return true;
		}

		return false;
	}  

	public void setData(String s)
	{
		/*HOW DO THIS THING!*/
	}

	public String readLine() throws IOException
	{
		int c1 = read();
		if (c1 == -1) throw new IOException("eof(1)");

		int c2 = read();
		if (c2 == -1) throw new IOException("eof(2)");

		String ret = "";
		while (c1 != '\r' || c2 != '\n')
		{
			ret = ret + (char)c1;
			c1 = c2;
			c2 = read();
			if (c2 == -1)
				throw new IOException("eof(3)");
		}

		return ret.length()>0 ? ret : null;
	}
}
