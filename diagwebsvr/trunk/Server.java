/******************************************************************
 * CODE FILE   : Server.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-12-2005
 * Beschrijving: Multithreaded Server - Server class
 */

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;

class Server
implements Runnable
{
	static final boolean DEBUG_MODE = false;

	private int port;
	private int backLog;
	private ServerSocket serverSocket;

	/* timeout on client connections */
	static int timeout = 5000;
	/* the web server's virtual root */
	static File virtualRoot = null;
	static HashMap contentTypeMap;
	static HashSet commandList;

	public Server(int port, int backLog, String codeBase)
	{
		this.port = port;
		this.backLog = backLog;
		virtualRoot = new File(codeBase);
		fillContentTypeMap(); // mapping of file extensions to content-types
		fillCommandList(); // create and fill list of commands
	}

	public void run()
	{
		try
		{
			writeDebug("De server is gestart.");
			serverSocket = new ServerSocket(port, backLog);

			int requestNumber = 0;
			while(true)
			{
				requestNumber++;
				Socket socket = serverSocket.accept();
				Service service = new Service(socket, requestNumber);
				Thread thread = new Thread(service);
				thread.start();
			}
		}
		catch (SocketException se)
		{
			writeDebug("\nSocket Closed");
		}
		catch (Exception e)
		{
			writeDebug(e);
		}
	}

	public void stop()
	{
		try
		{
			serverSocket.close();
		}
		catch (Exception e)
		{
			writeDebug(e);
		}
	}

	/* mapping of file extensions to content-types */
	private static void fillContentTypeMap()
	{
		contentTypeMap = new HashMap();
		contentTypeMap.put("", "content/unknown");
		contentTypeMap.put(".uu", "application/octet-stream");
		contentTypeMap.put(".exe", "application/octet-stream");
		contentTypeMap.put(".ps", "application/postscript");
		contentTypeMap.put(".zip", "application/zip");
		contentTypeMap.put(".sh", "application/x-shar");
		contentTypeMap.put(".tar", "application/x-tar");
		contentTypeMap.put(".snd", "audio/basic");
		contentTypeMap.put(".au", "audio/basic");
		contentTypeMap.put(".wav", "audio/x-wav");
		contentTypeMap.put(".gif", "image/gif");
		contentTypeMap.put(".jpg", "image/jpeg");
		contentTypeMap.put(".jpeg", "image/jpeg");
		contentTypeMap.put(".htm", "text/html");
		contentTypeMap.put(".html", "text/html");
		contentTypeMap.put(".text", "text/plain");
		contentTypeMap.put(".c", "text/plain");
		contentTypeMap.put(".cc", "text/plain");
		contentTypeMap.put(".c++", "text/plain");
		contentTypeMap.put(".h", "text/plain");
		contentTypeMap.put(".pl", "text/plain");
		contentTypeMap.put(".txt", "text/plain");
		contentTypeMap.put(".java", "text/plain");
	}

	private static void fillCommandList()
	{
		commandList = new HashSet();
		commandList.add("Connection");
		commandList.add("Accept-Language");
		commandList.add("User-Agent");
		commandList.add("Content-Length");
		commandList.add("Content-Type");
		commandList.add("Date");
		commandList.add("Accept");
		commandList.add("Accept-Encoding");
		commandList.add("Host");
		commandList.add("If-Modified-Since");
		commandList.add("Keep-Alive");
		commandList.add("Accept-Charset");
		commandList.add("Cache-Control");
		commandList.add("Pragma");
		commandList.add("Trailer");
		commandList.add("Transer-Encoding");
		commandList.add("Upgrade");
		commandList.add("Via");
		commandList.add("Warning");
		commandList.add("Authorization");
		commandList.add("Expect");
		commandList.add("From");
		commandList.add("If-Match");
		commandList.add("If-None-Match");
		commandList.add("If-Range");
		commandList.add("If-Unmodified-Since");
		commandList.add("Max-Forwards");
		commandList.add("Proxy-Authorization");
		commandList.add("Proxy-Authorization");
		commandList.add("Referer");
		commandList.add("Allow");
		commandList.add("Content-Encoding");
		commandList.add("Content-Language");
		commandList.add("Content-Location");
		commandList.add("Content-MD5");
		commandList.add("Content-Range");
		commandList.add("Expires");
		commandList.add("Last-Modified");     
		commandList.add("extension-header");
	}

	private void writeDebug(Object debug)
	{
		System.out.println(debug);
		System.out.println("");
	}
}
