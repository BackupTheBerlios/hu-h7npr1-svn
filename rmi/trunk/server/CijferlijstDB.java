/******************************************************************
 * CODE FILE   : CijferlijstDB.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-01-2006
 * Beschrijving: Class CijferlijstDB - Primary handling of database
 *               related tasks
 */
package server;

import java.sql.*;

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
		ResultSet rs = dbConnection.getResultset("select CijferID, StudentNaam, Cijfer from cijferlijst " +
																"order by StudentNaam");
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
		ResultSet rs = dbConnection.getResultset("update cijferlijst set StudentNaam='" + inData.studentName +
																"', Cijfer=" + String.valueOf(inData.cijfer) +
																" where CijferID=" + String.valueOf(inData.cijferID));
	}

	public void deleteOne(int id)
	throws SQLException
	{
		ResultSet rs = dbConnection.getResultset("delete from cijferlijst where CijferID=" + String.valueOf(id));
	}
}