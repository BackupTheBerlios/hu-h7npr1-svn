package server;

import java.util.*;
import java.sql.*;

import sun.jdbc.odbc.*;

class CijferlijstDB
{
	private final String DB_HOSTNAME = "127.0.0.1";
	private final String DB_DATABASE = "cijferlijst";
	private final String DB_USERNAME = "cluser";
	private final String DB_PASSWORD = "clpwd";

	private MySQL_Connector dbConnection;

	public CijferlijstDB()
	{
		System.out.println("About to instantiate MySQL_Connector()");
		dbConnection = new MySQL_Connector(DB_HOSTNAME, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
	}

	public ResultSet getAll()
	throws SQLException
	{
		ResultSet rs = dbConnection.getResultset("select CijferID, StudentNaam, Cijfer from cijferlijst");
		return rs;
	}

	public ResultSet getOne(int id)
	throws SQLException
	{
		ResultSet rs = dbConnection.getResultset("select CijferID, StudentNaam, Cijfer from cijferlijst where CijferID=" + String.valueOf(id));
		return rs;
	}

	public void insertOne(CijferlijstData inData)
	throws SQLException
	{
		ResultSet rs = dbConnection.getResultset("insert into cijferlijst (StudentNaam,Cijfer) values " +
																"('" + inData.studentName + "'," + String.valueOf(inData.cijfer) + ")");
	}

	public void updateOne(CijferlijstData inData)
	throws SQLException
	{
		ResultSet rs = dbConnection.getResultset("update table cijferlijst set StudentNaam='" + inData.studentName +
																"', Cijfer=" + String.valueOf(inData.cijfer) +
																" where CijferID=" + String.valueOf(inData.cijferID));
	}

	public void deleteOne(int id)
	throws SQLException
	{
		ResultSet rs = dbConnection.getResultset("delete from cijferlijst where CijferID=" + String.valueOf(id));
	}
}