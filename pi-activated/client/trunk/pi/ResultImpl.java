package pi;

import java.math.BigDecimal;

public class ResultImpl
implements Result
{
	private BigDecimal bigDecimal = null;
	private long time = 0L;

	public ResultImpl(BigDecimal bd, long t)
	{
		bigDecimal = bd;
		time = t;
	}

	public BigDecimal getBigDecimal() { return bigDecimal; }
	public long getTime() { return time; }
}