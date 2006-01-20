/******************************************************************
 * CODE FILE   : MySQL_Connector.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 20-01-2006
 * Beschrijving: Class MySQL_Connector - Handling of specific MySQL
 *               related tasks
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class MySQL_Connector
{
	private Connection conn = null;
	private String hostName = "";
	private String database = "";
	private String userName = "";
	private String password = "";

	/** Creates a new instance of MySQL_Connector */
	public MySQL_Connector(String hostName,
									String database,
									String userName,
									String password)
	{
		System.out.println("Constructor MySQL_Connector class");
		this.hostName = hostName;
		this.database = database;
		this.userName = userName;
		this.password = password;
		try
		{
			// The newInstance() call is a work around for some
			// broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}

	public Connection getConnection()
	{
		return conn;
	}

	public ResultSet executeQuery(String query)
	{
		System.out.println("Executing Query...");
		ResultSet returnSet = null;
		try
		{
			connect();
			Statement stmt = conn.createStatement();
			ResultSet rs;

			if (stmt.execute(query))
			{
				returnSet = stmt.getResultSet();
			}
		}
		catch (SQLException ex)
		{
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		//close();
		return returnSet;
	}

	public ResultSet getResultset(String query)
	throws SQLException
	{
		return executeQuery(query);
	}

	public void connect()
	throws SQLException
	{
		if (conn == null || conn.isClosed())
		{
			System.out.println("Connecting...");
			conn = DriverManager.getConnection("jdbc:mysql://" +
															hostName + "/" + database + "?" +
															"user=" + userName + "&" +
															"password=" + password);
			System.out.println("Connected!");
		}
	}

	public void close()
	{
		try
		{
			if (conn != null && !conn.isClosed())
				conn.close();
		} 
		catch (SQLException ex)
		{
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}        
	}
}