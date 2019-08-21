package lpc1700.stan;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:41:07
 * Петледержатель
 */
public class Loop
{
	public static short POS_MAX = 5000;
	public static short POS_MIN = -260;

	public short pos;		//[mm]
	public boolean work;	// петледержатель работает
	public boolean cool;	// охлаждение петледержателя

	public Loop()
	{
		work = true;	// петледержатель работает
		cool = true;	// охлаждение петледержателя включено
	}
}
