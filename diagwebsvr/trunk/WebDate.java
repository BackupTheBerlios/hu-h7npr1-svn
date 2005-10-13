/******************************************************************
 * CODE FILE   : WebDate.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 21-09-2005
 * Beschrijving: Datum format klasse tbv req/respondse headers
 */

import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class WebDate
{
	public static String toWebDate(Date inDate)
	{
		// Converts a Date to a String in webformat
		//
		// Input:  Date to be processed
		// Output: String representation of the given Date,
		//         converted to GMT, in webformat (dd MMM yyyy HH:mm:ss GMT)

		// public String toGMTString()
		// Deprecated. As of JDK version 1.1, replaced by DateFormat.format(Date date),
		// using a GMT TimeZone.
		// Creates a string representation of this Date object of the form:
		// d mon yyyy hh:mm:ss GMT

		DateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(inDate) + " GMT";
	}

	// Conversion String->Date not available (yet?)
	// public static Date toSysDate(String inDate)
	// {
	// 	Date returnDate = new Date();
	// 	return returnDate;
	// }
	
	public static String getCurrent()
	// Returns the current Date as a String in webformat
	//
	// Input:  None
	// Output: String representation of the current Date,
	//         converted to GMT, in webformat (dd MMM yyyy HH:mm:ss GMT)
	{
		Date currentDate = new Date();
		return toWebDate(currentDate);
	}

	public static String getDateFromLong(long longdate)
	{
		// Returns the Date as a String in webformat from a long containing milliseconds time value
		//
		// Input:  Long longdate representation a date in milliseconds
		// Output: String representation of the current Date,
		//         converted to GMT, in webformat (dd MMM yyyy HH:mm:ss GMT)            
		Date currentDate = new Date(longdate);
		return toWebDate(currentDate);            
	}
}
