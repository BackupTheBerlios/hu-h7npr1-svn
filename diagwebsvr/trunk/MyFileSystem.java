/******************************************************************
 * CODE FILE   : MyFileSystem.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 15-09-2005
 * Beschrijving: Some useful filesystem functions
 */

import java.io.File;

public class MyFileSystem
{
	public static String getCurrentDir()
	{
		File dir = new File(".");
		try
		{
			return dir.getCanonicalPath();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static String getParentDir()
	{
		File dir = new File("..");
		try
		{
			return dir.getCanonicalPath();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
