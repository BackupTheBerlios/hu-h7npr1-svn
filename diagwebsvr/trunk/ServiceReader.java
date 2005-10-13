/******************************************************************
 * CODE FILE   : ServiceReader.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 10-10-2005
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
		requests = new HashMap();

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
			else
				setRequestHeader(newline);
			if (debug)
				input.add(""  + x + ": " + newline);
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
		if (debug)
		{
			System.out.println("" + requestNumber + ": " + "Methode: " + method);
			System.out.println("" + requestNumber + ": " + "Domainname: " + domainName);
			System.out.println("" + requestNumber + ": " + "requestUrl: " + requestUrl);
			System.out.println("" + requestNumber + ": " + "httpVersion: " + httpVersion);
			System.out.println("");
		}
	}

	public void setRequestHeader(String s)
	{
		StringTokenizer st = new StringTokenizer(s, ":");
		String rh = st.nextToken();

		requests.put(rh, st.nextToken("blablbalbalablabla"));
		if (debug)
			System.out.println("setRequestHeader: " + rh );
	}

	public void handleRequestHeaders()
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

/*		if ((request = (String) requests.get("Accept-Charset")) != null){}
		if ((request = (String) requests.get("Cache-Control")) != null){}        
		if ((request = (String) requests.get("Pragma")) != null){}
		if ((request = (String) requests.get("Trailer")) != null){}
		if ((request = (String) requests.get("Transer-Encoding")) != null){}
		if ((request = (String) requests.get("Upgrade")) != null){}
		if ((request = (String) requests.get("Via")) != null){}
		if ((request = (String) requests.get("Warning")) != null){}
		if ((request = (String) requests.get("Authorization")) != null){}
		if ((request = (String) requests.get("Expect")) != null){}
		if ((request = (String) requests.get("From")) != null){}
		if ((request = (String) requests.get("If-Match")) != null){}
		if ((request = (String) requests.get("If-None-Match")) != null){}
		if ((request = (String) requests.get("If-Range")) != null){}
		if ((request = (String) requests.get("If-Unmodified-Since")) != null){}
		if ((request = (String) requests.get("Max-Forwards")) != null){}
		if ((request = (String) requests.get("Proxy-Authorization")) != null){}
		if ((request = (String) requests.get("Proxy-Authorization")) != null){}
		if ((request = (String) requests.get("Referer")) != null){}
		if ((request = (String) requests.get("TE")) != null){}
		if ((request = (String) requests.get("Allow")) != null){}
		if ((request = (String) requests.get("Content-Encoding")) != null){}
		if ((request = (String) requests.get("Content-Language")) != null){}
		if ((request = (String) requests.get("Content-Location")) != null){}
		if ((request = (String) requests.get("Content-MD5")) != null){}
		if ((request = (String) requests.get("Content-Range")) != null){}
		if ((request = (String) requests.get("Expires")) != null){}
		if ((request = (String) requests.get("Last-Modified")) != null){}
		if ((request = (String) requests.get("extension-header")) != null){}
*/
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
