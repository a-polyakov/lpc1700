package lpc1700.stan;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:55:09
 * Гидравлическая клеть чистовой группы
 * <p/>
 * максимальное усилие 2650
 */
public class KletHidro extends KletChistovaya
{
	public KletHidro(String name)
	{
		super(name);
	}

	public short pos_cyl_dr;	// [micron] раствор на нижним валке на стороне маш зала
	public short pos_cyl_op;	// [micron] раствор на нижним валке на стороне оператора
	public short pos_cyl_ref;	// [micron] заданный раствор на нижним валке
}