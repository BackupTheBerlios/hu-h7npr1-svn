package javaappletth7npr1rmi;

import java.util.*;
import java.sql.*;
import sun.jdbc.odbc.*;

class CijferlijstDB extends BaseDB
{
	public boolean verwijder(IDataClass cldata) throws SQLException
	{
		dbConnection.executeQuery("delete from cijferlijst where CijferID = " + ((CijferlijstData) cldata).cijferID);
                return true;
	}

	public boolean slaOp(IDataClass cldata) throws SQLException
	{
		if(((CijferlijstData) cldata).cijferID == 0)
		{
			return insert((CijferlijstData) cldata);
		}
		else
		{
			return update((CijferlijstData) cldata);
		}
	}
	
	private boolean insert(CijferlijstData cf) throws SQLException
	{		
		String sql = "INSERT INTO cijferlijst (StudentNaam, Cijfer) VALUES('" + cf.studentName +"' , " + cf.cijfer + "')";	
		dbConnection.executeQuery(sql);
                
                return true;
	}
	
	private boolean update(CijferlijstData cf) throws SQLException
	{
		String sql = "UPDATE cijferlijst ";		
		sql += "SET StudentNaam = " + cf.studentName;
		sql += ", Cijfer = " + cf.cijfer;
		sql += " WHERE CijferID = " + cf.cijferID;
		dbConnection.executeQuery(sql);
                
                return true;
	}
	
        public boolean bestaatNaam(String s) throws SQLException
        {
		ResultSet rs = dbConnection.getResultset("select CijferID, StudentNaam, Cijfer from cijferlijst where studentNaam = '" + s + "'");
		Hashtable data = new Hashtable();            
                if (rs.next()){
                    return true;
                }
                return false;
        }
        
	public Hashtable zoek() throws SQLException
	{	
		ResultSet rs = dbConnection.getResultset("select CijferID, StudentNaam, Cijfer from cijferlijst");
		Hashtable data = new Hashtable();
		while(rs.next())
		{
			CijferlijstData cf = load(rs);
			data.put("" + cf.studentName, cf);
		}
		return data;
	}
	
	public IDataClass haalOp(int CijferID) throws SQLException
	{	
		ResultSet rs = dbConnection.getResultset("SELECT * FROM cijferlijst WHERE CijferID=" + CijferID);		
		CijferlijstData cf = load(rs);
		return cf;
	}
	
	
	private CijferlijstData load(ResultSet rs) throws SQLException
	{
		CijferlijstData cf =  new CijferlijstData();
		cf.cijferID = rs.getInt("CijferID");
		cf.studentName = rs.getString("StudentNaam");
		cf.cijfer = rs.getDouble("Cijfer");
		return cf;
	}
}