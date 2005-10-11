/******************************************************************
 * CODE FILE   : MyOutputStream.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-09-2005
 * Beschrijving: Gecustomiseerde versie van OutputStream
 */

import java.io.*;

public class MyOutputStream 
extends OutputStream
{
	private MyFrame myFrame = null;

	/** Creates a new instance of MyOutputStream */
	public MyOutputStream(MyFrame mf)
	{
		myFrame = mf;
	}

	public void write(int b)
	throws IOException
	{
		myFrame.addText("" + (char)b);
	}
}
