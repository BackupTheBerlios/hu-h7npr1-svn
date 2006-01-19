package server;

import java.util.*;
public class CijferlijstData implements IDataClass
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