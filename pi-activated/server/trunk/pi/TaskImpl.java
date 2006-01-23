package pi;

import java.math.BigDecimal;

public class TaskImpl
implements Task
{
	private static final BigDecimal  ZERO = BigDecimal.valueOf(0);
	private static final BigDecimal   ONE = BigDecimal.valueOf(1);
	private static final BigDecimal  FOUR = BigDecimal.valueOf(4);
	private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN;

	private int digits = 3;

	public TaskImpl(int x)
	{
		digits = x;
	}
	
	public Result execute()
	{
		long t0 = System.currentTimeMillis();
		int scale = digits + 5;
		BigDecimal arctan1_5 = arctan(5, scale);
		BigDecimal arctan1_239 = arctan(239, scale);
		BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);
		BigDecimal ret = pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
		return new ResultImpl(ret, System.currentTimeMillis()-t0);
	}

	private BigDecimal arctan(int inverseX, int scale)
	{
		BigDecimal  invX = BigDecimal.valueOf(inverseX);
		BigDecimal  invX2 = BigDecimal.valueOf(inverseX * inverseX);
		BigDecimal  numer = ONE.divide(invX, scale, roundingMode);
		BigDecimal result = numer;
		BigDecimal term;
		int i = 1;
		do
		{
			numer = numer.divide(invX2, scale, roundingMode);
			int denom = 2 * i + 1;
			term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
			result = ((i % 2) != 0) ? result.subtract(term) : result.add(term);
			i++;
		}
		while (term.compareTo(ZERO) != 0);
		return result;
	}
}