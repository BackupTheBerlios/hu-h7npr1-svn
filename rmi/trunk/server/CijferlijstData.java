/******************************************************************
 * CODE FILE   : CijferlijstData.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-01-2006
 * Beschrijving: Class CijferlijstData - Data class
 */
package server;

public class CijferlijstData
{
	public int cijferID = 0;
	public String studentName;
	public double cijfer;
	
	public static CijferlijstData[] arrayAdd(CijferlijstData[] inArray, CijferlijstData addData)
	{
		int arraySize = 0;
		if (inArray != null)
			arraySize = inArray.length;
		if (addData == null)
			return inArray;
		CijferlijstData[] outArray = new CijferlijstData[arraySize+1];
		for (int index = 0; index < arraySize; index++)
		{
			outArray[index] = inArray[index];
		}
		outArray[arraySize] = addData;
		return outArray;
	}
}