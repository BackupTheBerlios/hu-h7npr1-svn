import java.io.FileInputStream;
import java.io.ObjectInputStream;

import pi.*;

public class Main
{
	public static void main(String[] arg)
	throws Exception
	{
		FileInputStream fis = new FileInputStream("PiRef");
		ObjectInputStream ois = new ObjectInputStream(fis);
		Pi pi = (Pi)ois.readObject();

		Task task = new TaskImpl(50);

		Result result = pi.perform(task);
		System.out.println(result.getBigDecimal());
		System.out.println(result.getTime());    
	}
}