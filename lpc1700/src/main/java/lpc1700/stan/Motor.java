package lpc1700.stan;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:46:41
 * Электро двигатель клети
 * <p/>
 * Мощности двигателей, кВт
 * черновой окаленоломатель	4000
 * 1-4а						4000
 * 5-9						8000
 * 10						5000
 * Число оборотов в минуту
 * черновой окаленоломатель	600
 * 1-4а						600
 * 5,6						175-330
 * 7,8						110-240
 * 9,10						175-330
 */
public class Motor
{
	public static short I_MIN = 0;
	public static short I_MAX = 6000;
	public static short P_MIN = 1000;
	public static short P_MAX = 4000;
	public static short SPEED_MAX = 3000;	// [tr/min*10] максимальная скорость клети
	public static short SPEED_MIN = 0;		// [tr/min*10] минимальная скорость
	public short I;			// сила тока, A
	public short V;			// напряжение, V
	public short P;			// мощность, Вт
	public short speed;		// скорость вращения, количество оборотов за 10 минут
	public short ref_speed;	// заданная скорость вращения, оборотов за 10 минут
	public float reductor;	// коэфициент изменения скорости редуктором

	public Motor()
	{
		I = 0;
		V = 0;
		P = 0;
		speed = 3000;
		ref_speed = 3000;
		reductor = 1;
	}
}
