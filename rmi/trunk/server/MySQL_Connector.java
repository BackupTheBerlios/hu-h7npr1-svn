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
public class MySQL_Connector {
    
    public Connection conn;
    
    public static void main(String[] args) {
        
       MySQL_Connector ms = new MySQL_Connector();
        
    }
    
    /** Creates a new instance of MySQL_Connector */
    public MySQL_Connector() {

        try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/cijferlijst?user=root&password=test");
        } 
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        catch (Exception e) { e.getMessage(); }
        
    }
    
    public Connection getConnection(){
        return conn;
    }
    
    public ResultSet executeQuery(String s){
        try {
            if (conn.isClosed()){
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/cijferlijst?user=root&password=test");
            }
            Statement stmt = conn.createStatement();
            ResultSet rs;

            if (stmt.execute(s)) {
                return stmt.getResultSet();
            }
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        
        return null;
    }
    public ResultSet getResultset(String s) throws SQLException
    {
        return executeQuery(s);
    }
 
    public void close(){
        try {
            if (!conn.isClosed())
                conn.close();
        } 
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }        
    }
}
