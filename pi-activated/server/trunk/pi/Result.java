package pi;

import java.math.BigDecimal;
import java.io.Serializable;

public interface Result
extends Serializable
{
	BigDecimal getBigDecimal(); // get the result
	long getTime();             // get the calculation time
}