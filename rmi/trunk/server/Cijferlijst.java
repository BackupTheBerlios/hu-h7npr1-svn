package server;

import java.util.*;
import java.sql.*;
import sun.jdbc.odbc.*;

public class Cijferlijst {
    
    private CijferlijstDB cfdb;
    private Hashtable cijfers;
    
    public Cijferlijst(){
        cfdb = new CijferlijstDB();
    }
    
    public boolean store(String naam, double cijfer){
        if (cijfers.contains(naam)){
            CijferlijstData cfd = (CijferlijstData) cijfers.get(naam);
            if (!(cfd.studentName.equals(naam) && cfd.cijfer == cijfer)){
                cfd.studentName = naam;
                cfd.cijfer = cijfer;
                try {
                    cfdb.slaOp(cfd);
                }
                catch (SQLException ex) { 
                    ex.getMessage(); 
                    return false;
                }
            }
        }
        else {
            try {
                if (cfdb.bestaatNaam(naam)) return false;
                CijferlijstData cfd = new CijferlijstData();
                cfd.studentName = naam;
                cfd.cijfer = cijfer;
                cfdb.slaOp(cfd);                     
            }
            catch (SQLException ex) { 
                ex.getMessage(); 
                return false;
            }           
        }
        return true;
    }
    
    public Hashtable getHashtable(){
        try {
            cijfers = cfdb.zoek();
        }
        catch (SQLException ex) { ex.getMessage(); }
        
        Hashtable temp = new Hashtable();
        if (!cijfers.isEmpty()){

                CijferlijstData tempdata;
                for (Enumeration e = cijfers.elements(); e.hasMoreElements();){
                        tempdata = (CijferlijstData)e.nextElement();
                        temp.put(tempdata.studentName, Double.toString(tempdata.cijfer));
                }
                return temp;
        }
        else {
                return null;	
        }        
        
    }
}