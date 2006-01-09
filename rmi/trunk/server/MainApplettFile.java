/*
 * MainApplettFile.java
 *
 * Created on 20 november 2005, 21:28
 */

package server;

import java.awt.Graphics;
import java.applet.Applet;
import java.net.URL;
import java.rmi.Naming;
import java.util.*;


/**
 *
 * @author  Stephen
 */
public class MainApplettFile extends java.applet.Applet {

  private URL    codebase  = null;
  private String service   = null;
  private String reference = null;

  public Hashtable students;
    
    /** Initializes the applet MainApplettFile */
    public void init() {
        
        setLayout(new java.awt.BorderLayout());
        
        students = new Hashtable();
        
        try
        {
          codebase = getCodeBase();
          String host = codebase.getHost();
          service = "rmi://"+host+":1099/"+CijferlijstService.refName;
          CijferlijstService cs = (CijferlijstService)Naming.lookup(service);
          
          students = cs.getStudents();
          
          cs.store("Testnaam Achternaam1", 5.6);
          cs.store("Testnaam Achternaam2", 5.7);
          cs.store("Testnaam Achternaam3", 5.8);
          
        }
        catch (Exception e)
        {
          System.out.println(e);
        }            
    }
   
}
