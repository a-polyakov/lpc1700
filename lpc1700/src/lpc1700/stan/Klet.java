package lpc1700.stan;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:37:31
 * клеть четырех валковая
 * <p/>
 * скорости черновой группы клетей постоянны ? какие скорости
 * Число оборотов в минуту
 * черновой окаленоломатель	600
 * 1-4а						600
 * скорость рольганга между клетями постоянна или нет как управляется
 */
public class Klet
{
	public static short force_max = 2000;	// [ton]	максимально допустимая нагрузка на клеть
	public static short force_min = 500;	// [ton]	минимальная нагрузка клети

	public String name;
	public short op_force;	// [ton]	усилие на валки на стороне оператора
	public short dr_force;	// [ton]	усилие на валки на стороне маш зала
	public short op_pos;	// [10*micron]	раствор на валки на стороне оператора

	public short zer_force;	// [ton] усилие достигнутое при "обнулении" клети

	public boolean work;	// клеть работает
	public boolean open;	// клеть открыта

	public Motor motor;

	public Strip strip;		// штука катаемая клетью

	public Klet(String name)
	{
		this.name = name;
		op_force = 2000;	// 2000 t
		op_pos = 0;			// 0 mm
		work = true;		// работает
		open = false;		// закрыта
		motor = new Motor();
		strip = null;
	}

	public boolean sis()
	{
		return motor.P >= Motor.P_MIN;
	}
}
