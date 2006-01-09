/*Class: BaseDB
 *Author: Eelco Duinsbergen
 *
 *Comments:
 *Base DB class voor het ophalen van data uit de tabellen van de applicatie.
 *Alle DB classes die voor de applicatie gedefinieerd zijn
 *dienen van deze class te erven. 
 *
 *De baseDB onderhoud de verbinding naar de database en voorziet en methodes
 *voor select, update, insert en delete operaties.
 */
package server;

import java.util.*;
import java.sql.*;
import sun.jdbc.odbc.*;

abstract class BaseDB 
{
	protected MySQL_Connector dbConnection;
	
	public BaseDB()
	{
		dbConnection = new MySQL_Connector();
	}
			
	public abstract IDataClass haalOp(int id) throws SQLException;
	public abstract boolean slaOp(IDataClass data) throws SQLException;
        public abstract Hashtable zoek() throws SQLException;
	public abstract boolean verwijder(IDataClass data) throws SQLException;
}
