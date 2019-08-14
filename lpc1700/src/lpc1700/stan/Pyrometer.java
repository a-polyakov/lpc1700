package lpc1700.stan;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 12.09.2007
 * Time: 14:50:49
 * Пирометр
 */
public class Pyrometer
{
	public short max_t;		// максимально возможная темпиратура на пирометре, °C
	public short min_t;		// минимально возможная темпиратура на пирометре, °C
	public boolean work;	// пирометр работает
	private short t;		// измеряная температура, °C
	private short ts;		// средняя температура прошедшей полосы, °C
	private boolean sis;	// под пирометром находится штука
	public Strip strip;		// штука находящяяся под пирометром

	public Pyrometer(int min, int max)
	{
		max_t = (short) max;
		min_t = (short) min;
		t = 0;
		ts = 0;
		work = true;
		strip = null;
	}

	public void setT(short t)
	{
		this.t = t;
		sis = t >= min_t;
	}

	public short getT()
	{
		if (sis)
			return t;
		else
			return ts;
	}

	public void setTsred(short t)
	{
		ts = t;
	}

	// под пирометром находится штука
	public boolean sis()
	{
		return sis;
	}
}
