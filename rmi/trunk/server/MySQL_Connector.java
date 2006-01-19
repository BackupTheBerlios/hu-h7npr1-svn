/*
 * MySQL_Connector.java
 *
 * Created on 21 november 2005, 19:10
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
/**
 *
 * @author Stephen
 */
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

	public ResultSet executeQuery(String s)
	{
		System.out.println("Executing Query...");
		ResultSet returnSet = null;
		try
		{
			connect();
			Statement stmt = conn.createStatement();
			ResultSet rs;

			if (stmt.execute(s))
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

	public ResultSet getResultset(String s)
	throws SQLException
	{
		return executeQuery(s);
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