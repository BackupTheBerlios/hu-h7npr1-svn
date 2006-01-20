/******************************************************************
 * CODE FILE   : Cijferlijst.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-01-2006
 * Beschrijving: Class Cijferlijst - Business class
 */
package server;

import java.sql.*;

public class Cijferlijst
{
	private CijferlijstDB cfdb;
	//private Hashtable cijfers;

	public Cijferlijst()
	{
		System.out.println("About to instantiate CijferlijstDB()");
		cfdb = new CijferlijstDB();
	}

	public CijferlijstData[] getCijferlijst()
	{
		CijferlijstData[] resultSet = null;
		try
		{
			ResultSet rs = cfdb.getAll();
			while(rs.next())
			{
				resultSet = CijferlijstData.arrayAdd(resultSet, parseRS(rs));
			}
			return resultSet;
		}
		catch (SQLException ex)
		{
			ex.getMessage(); 
		}
		return null;
	}

	public CijferlijstData getCijfer(int id)
	{
		CijferlijstData resultSet = null;
		try
		{
			ResultSet rs = cfdb.getOne(id);
			if (rs.next())
			{
				resultSet = parseRS(rs);
				return resultSet;
			}
		}
		catch (SQLException ex)
		{
			ex.getMessage(); 
		}
		return null;
	}

	public void setCijfer(CijferlijstData cijferData)
	{
		try
		{
			if (cijferData.cijferID == 0)
				cfdb.insertOne(cijferData);
			else
				cfdb.updateOne(cijferData);
		}
		catch (SQLException ex)
		{
			ex.getMessage(); 
		}
	}
	
	public void deleteCijfer(int id)
	{
		try
		{
			cfdb.deleteOne(id);
		}
		catch (SQLException ex)
		{
			ex.getMessage(); 
		}
	}

	private CijferlijstData parseRS(ResultSet rs)
	throws SQLException
	{
		CijferlijstData cf =  new CijferlijstData();
		cf.cijferID = rs.getInt("CijferID");
		cf.studentName = rs.getString("StudentNaam");
		cf.cijfer = rs.getDouble("Cijfer");
		return cf;
	}
}