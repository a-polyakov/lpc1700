package lpc1700.stan;

import lpc1700.Stan;
import lpc1700.Logs;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:44:20
 * Сляб - раскат - полоса - рулон
 * <p/>
 * когда вычеслили положение головы запоминаем время
 * по времени задержки на участках определять физические сбои и удаление
 */
public class Strip
{
	// размеры исходного сляба
	public static final float H_IN_MIN = 0.12f;		// толщина:	0.120..0.185 м
	public static final float H_IN_MAX = 0.185f;
	public static final float W_IN_MIN = 0.98f;		// ширина:	0.980..1.570 м
	public static final float W_IN_MAX = 1.57f;
	public static final float L_IN_MIN[] =
			new float[]{2.8f, 3.8f, 5.2f};			// длина:	2.8..3.1 м (3.8..4.8 5.2..6.2)
	public static float L_IN_MAX[] =
			new float[]{3.1f, 4.8f, 6.2f};
	// размер готовой полосы
	public static final float H_OUT_MIN = 0.0015f;	// толщина:	1,5..8 мм
	public static final float H_OUT_MAX = 0.008f;
	public static final float W_OUT_MIN = 1;		// ширина:	1000..1540 мм
	public static final float W_OUT_MAX = 1.54f;

	private float in_length;						// длина сляба, м
	private float in_width;							// ширина сляба, м
	private float in_height;						// толщина сляба, м
	private float f4a_length;						// длина подката, м
	private float f4a_height;						// толщина подката, м
	private float f_5_height;						// толщина полосы после 5 клети, м
	private float f_6_height;						// толщина полосы после 6 клети, м
	private float f_7_height;						// толщина полосы после 7 клети, м
	private float f_8_height;						// толщина полосы после 8 клети, м
	private float f_9_height;						// толщина полосы после 9 клети, м
	private float out_width;						// ширина полосы, м
	private float out_height;						// заданное значений толщины полосы

	private float speed;							// скорость потока, m^3/c
	public short h[];								// массив изменения толщины полосы от номинальой, [-10%(-2^16+1) 10%(2^16)]
	public int index_h;								// номер текущего сигнала
	//public short h_r[];							// массив толщин участков
	//public short t_r[];							// массив температур участков
	private float t1_sred;							// средняя температура на 1 пироматре
	private int t1_index;							// количество сигналов от пирометра
	private float t2_sred;							// средняя температура на 2 пироматре
	private int t2_index;							// количество сигналов от пирометра
	private float t3_sred;							// средняя температура на 3 пироматре
	private int t3_index;							// количество сигналов от пирометра
	private float t_out_sred;						// средняя температура на 4 или 5 пироматре
	private int t_out_index;						// количество сигналов от пирометра
	private float t6_sred;							// средняя температура на 6 пироматре
	private int t6_index;							// количество сигналов от пирометра

	private Party party;							// из какой партии
	private int id;									// порядковый номер в партии
	public long timeStart;							// время начала прокатки (Сигнал T1)
	public long timeT2;
	public long timeT3;
	private long timeOut;							// время сигнала T4 или T5
	private long timeT6;							// время сигнала T6
	public long timeEnd;							// время последнего изменения sis, время оканчания прокатки (!!Сигнал окончания смотки)
	public float head;								// текущяя позиция начала полосы
	public float V3;								// обьем сляба [m^3]

	private String status;

	private byte posProcent[];

	private Stan stan;

	public Strip(Stan stan)
	{
		this.stan = stan;
		speed = 0.024f;
		h = new short[4000];
		index_h = 0;
		//h_r = new short[1000];
		//t_r = new short[1000];
		t1_sred = 0;
		t1_index = 0;
		t2_sred = 0;
		t2_index = 0;
		t3_sred = 0;
		t3_index = 0;
		t_out_sred = 0;
		t_out_index = 0;
		t6_sred=0;
		t6_index=0;

		party = null;
		id = 0;
		// самый большой сляб
		in_length = L_IN_MAX[2];
		in_width = W_IN_MAX;
		in_height = H_IN_MAX;
		f4a_length = 0;
		f4a_height = 0.025f;	// !!
		out_width = W_OUT_MAX;
		out_height = H_OUT_MAX;

		V3 = in_height * in_width * in_length;

		head = Stan.locationF4a; // !

		posProcent = new byte[]{16, 17, 17, 17, 17, 16};
		calcHeight();
		timeStart = Stan.timeMaster.toLong();
		timeEnd = timeStart;
		status = "поступила";
	}

	public Strip(InputStream in) throws IOException
	{
		byte bufer[]=new byte[512];
		in.read(bufer);
		String s=new String(bufer);
		fromString(s);
	}

	//
	public void setParty(Party party)
	{
		this.party = party;
		// ! длиный сляб
		in_width = party.in_width;
		in_height = party.in_height;
		f4a_height = party.f4a_height;
		out_width = party.out_width;
		out_height = party.out_height;
		id = party.getOutStrip() + 1;
		// пересчитать объем
		if (f4a_length > 0)
			// объем подката
			V3 = f4a_height * out_width * f4a_length;
		else
			// объем сляба
			V3 = in_height * in_width * in_length;
		posProcent = party.getPosProcent();
		calcHeight();
	}

	public Party getParty()
	{
		return party;
	}

	// порядковый номер полосы в серии
	public int getID()
	{
		return id;
	}

	public float getHeight()
	{
		if (head > Stan.locationF10)
			return out_height;
		else if (head > Stan.locationF_9)
			return f_9_height;
		else if (head > Stan.locationF_8)
			return f_8_height;
		else if (head > Stan.locationF_7)
			return f_7_height;
		else if (head > Stan.locationF_6)
			return f_6_height;
		else if (head > Stan.locationF_5)
			return f_5_height;
		else
			return f4a_height;
	}

	public float getWinth()
	{
		if (head > Stan.locationF4a)
			return out_width;
		return in_width;
	}

	// Длина полосы
	// ! черновая группа
	public float getLength()
	{
		float end = head;

		float v_current = V3;
		float arrayLocation[] = new float[]
				{Stan.locationF4a,
						Stan.locationF_5, Stan.locationF_6, Stan.locationF_7,
						Stan.locationF_8, Stan.locationF_9, Stan.locationF10};
		float arrayHeight[] = new float[]
				{f4a_height,
						f_5_height, f_6_height, f_7_height, f_8_height, f_9_height, out_height};
		int i = arrayLocation.length - 1;
		// за какой позицией находится голова
		while (i > 0 && end <= arrayLocation[i])
			i--;
		if (i > 0)
		{	// дальше первой метки
			while (i > 0 && v_current > 0)
			{	// уменьшаем текущий обьем переходим к следующему участку
				v_current -= (end - arrayLocation[i]) * arrayHeight[i] * out_width; //
				end = arrayLocation[i];
				i--;
			}
		}
		// находим положение хвоста
		end -= v_current / arrayHeight[i] / out_width; //
		return head - end;
	}

	// !!
	public String getStatus()
	{
		return status;
	}

	// !!!
	public float getSpeed(float location)
	{
		if (location < Stan.locationF4a)
			return speed / f4a_height / out_width;
		if (location < Stan.locationF_5)
			return speed / f4a_height / out_width;
		if (location < Stan.locationF_6)
			return speed / f_5_height / out_width;
		if (location < Stan.locationF_7)
			return speed / f_6_height / out_width;
		if (location < Stan.locationF_8)
			return speed / f_7_height / out_width;
		if (location < Stan.locationF_9)
			return speed / f_8_height / out_width;
		if (location < Stan.locationF10)
			return speed / f_9_height / out_width;
		return speed / out_height / out_width;
	}

	// положение хвоста полосы
	public float end()
	{
		return head - getLength();
	}

	// зная положение хвоста найти положение головы
	public void calcHead(float endStripLocation)
	{
		float v_current, v;
		float arrayLocation[] = new float[]
				{Stan.locationF4a,
						Stan.locationF_5, Stan.locationF_6, Stan.locationF_7,
						Stan.locationF_8, Stan.locationF_9, Stan.locationF10};
		float arrayHeight[] = new float[]
				{f4a_height,
						f_5_height, f_6_height, f_7_height, f_8_height, f_9_height, out_height};
		int i = 1;
		v_current = V3;
		// перед какой позицией находится хвост
		while (i < arrayLocation.length - 1 && endStripLocation >= arrayLocation[i + 1])
			i++;
		if (i < arrayLocation.length - 1)
		{	// хваост до 10 клети
			while (i < arrayLocation.length - 1 && v_current > (v = (arrayLocation[i] - endStripLocation) * arrayHeight[i - 1] * out_width))
			{	// уменьшаем текущий обьем переходим к следующему участку
				v_current -= v;
				endStripLocation = arrayLocation[i];
				i++;
			}
		}
		// находим положение головы
		endStripLocation += v_current / arrayHeight[i - 1] / out_width;
		if (head < endStripLocation)
			head = endStripLocation;
		// !! реальная длина по какойто пречене больше расчетной
		// или обьем или толщина ошибочны
	}

	// определить выходные толщины для каждой из клетей
	public void calcHeight()
	{
		float f = f4a_height / out_height;
		f_5_height = f4a_height / (float) Math.pow(f, posProcent[0] / 100.f);
		f_6_height = f_5_height / (float) Math.pow(f, posProcent[1] / 100.f);
		f_7_height = f_6_height / (float) Math.pow(f, posProcent[2] / 100.f);
		f_8_height = f_7_height / (float) Math.pow(f, posProcent[3] / 100.f);
		f_9_height = f_8_height / (float) Math.pow(f, posProcent[4] / 100.f);
	}

	// 2. толщина подката на основе показаний клети 4а
	public void height4a()
	{
		// толщина = усилия 4а, раствор, ширина
		// !! расчет выходных толщин клетей

	}

	// 3. температура на основе токазаний T1
	public void t1Begin()
	{	// полоса подошла к пирометру
		timeEnd = Stan.timeMaster.toLong();
		head = Stan.locationT1;
		status = "T1";
	}

	public void t1()
	{	// данные от пирометра
		// ! прогноз температуты для т2
		t1_sred += stan.t1.getT();
		t1_index++;
		// расчет температуры на всех участках
		// t2
		// t3
		// F5.t
		// F5.setTerm()
	}

	public void t1End()
	{	// полоса прошла пирометр
		t1_sred /= t1_index;	// стедняя температура
		stan.t1.setTsred((short) t1_sred);
		// время нахождения полосы под пирометром
		long time = Stan.timeMaster.toLong() - timeEnd;
		// линейная скорость после клети 4а, м/с
		// ! float speed = скорость_двигателя * (float)Math.PI * диаметр_валка;
		float speed = 2.9f;
		// длина подката по данным T1
		f4a_length = speed * time / 1000;
		// объем подката
		V3 = f4a_height * out_width * f4a_length;
		// скорость потока
		this.speed = V3 * 1000 / time;
		timeEnd += time;
		calcHead(Stan.locationT1);
		status = "прошла Т1";
	}

	// 4. !!!! полоса проходит ножницы
	public void trim()
	{
	}

	// 5. температура на основе токазаний T2
	public void t2Begin()
	{	// полоса подошла к пирометру
		timeT2 = Stan.timeMaster.toLong();
		timeEnd = timeT2;
		head = Stan.locationT2;
		status = "T2";
	}

	public void t2()
	{	// данные от пирометра
		t2_sred += stan.t2.getT();
		t2_index++;
		// ! прогноз температуты для т3
		// ! если полученая температера не совподает с расчетной
		// расчет температуры на всех участках
		// t3
		// F5.t
		// F5.setTerm()
	}

	// !! добавить проверку по времени возможно что полоса уже прошла
	public void t2End()
	{	// полоса прошла пирометр
		t2_sred /= t2_index;	// стедняя температура
		stan.t2.setTsred((short) t2_sred);
		timeEnd = Stan.timeMaster.toLong();
		long time = timeEnd - timeT2;
		speed = V3 * 1000 / time;
		calcHead(Stan.locationT2);
		status = "прошла Т2";
	}

	// 6. температура на основе токазаний T3
	public void t3Begin()
	{	// полоса подошла к пирометру
		timeT3 = Stan.timeMaster.toLong();
		timeEnd = timeT3;
		head = Stan.locationT3;
		status = "T3";
	}

	public void t3()
	{	// данные от пирометра
		t3_sred += stan.t3.getT();
		t3_index++;
		// ! прогноз температуты для 5 клети
		// ! если полученая температера не совподает с расчетной
		// расчет температуры на всех участках
		// F5.t
		// F5.setTerm()
	}

	public void t3End()
	{	// полоса прошла пирометр
		t3_sred /= t3_index;	// стедняя температура
		stan.t3.setTsred((short) t3_sred);
		timeEnd = Stan.timeMaster.toLong();
		long time = timeEnd - timeT3;
		speed = V3 * 1000 / time;
		calcHead(Stan.locationT3);
		status = "прошла Т3";
	}

	// 7. усилия клети 5
	public void klet5Begin()
	{	// полоса зашла в клеть
		timeEnd = Stan.timeMaster.toLong();
		head = Stan.locationF_5;
		status = "F5";
	}

	public void klet5()
	{	// полоса в клети
		// полученые усилия не равны расчетным
		// F5.t
		// F5.h_out
		// F6.setTerm
	}

	public void klet5End()
	{	// полоса вышла из клети
		timeEnd = Stan.timeMaster.toLong();
		calcHead(Stan.locationF_5);
		status = "прошла F5";
	}

	// 8. усилия клети 6
	public void klet6Begin()
	{	// полоса зашла в клеть
		timeEnd = Stan.timeMaster.toLong();
		head = Stan.locationF_6;
		status = "F6";
	}

	public void klet6()
	{	// полоса в клети
		// полученые усилия не равны расчетным
		// F5.t
		// F5.h_out
		// F6.setTerm
	}

	public void klet6End()
	{	// полоса вышла из клети
		timeEnd = Stan.timeMaster.toLong();
		calcHead(Stan.locationF_6);
		status = "прошла F6";
	}

	// 9. усилия клети 7
	public void klet7Begin()
	{	// полоса зашла в клеть
		timeEnd = Stan.timeMaster.toLong();
		head = Stan.locationF_7;
		status = "F7";
	}

	public void klet7()
	{	// полоса в клети
		// полученые усилия не равны расчетным
		// F5.t
		// F5.h_out
		// F6.setTerm
	}

	public void klet7End()
	{	// полоса вышла из клети
		timeEnd = Stan.timeMaster.toLong();
		calcHead(Stan.locationF_7);
		status = "прошла F7";
	}

	// 10. усилия клети 8
	public void klet8Begin()
	{	// полоса зашла в клеть
		timeEnd = Stan.timeMaster.toLong();
		head = Stan.locationF_8;
		status = "F8";
	}

	public void klet8()
	{	// полоса в клети
		// полученые усилия не равны расчетным
		// F5.t
		// F5.h_out
		// F6.setTerm
	}

	public void klet8End()
	{	// полоса вышла из клети
		timeEnd = Stan.timeMaster.toLong();
		calcHead(Stan.locationF_8);
		status = "прошла F8";
	}

	// 11. усилия клети 9
	public void klet9Begin()
	{	// полоса зашла в клеть
		timeEnd = Stan.timeMaster.toLong();
		head = Stan.locationF_9;
		status = "F9";
	}

	public void klet9()
	{	// полоса в клети
		// полученые усилия не равны расчетным
		// F5.t
		// F5.h_out
		// F6.setTerm
	}

	public void klet9End()
	{	// полоса вышла из клети
		timeEnd = Stan.timeMaster.toLong();
		calcHead(Stan.locationF_9);
		status = "прошла F9";
	}

	// 12. усилия клети 10
	public void klet10Begin()
	{	// полоса зашла в клеть
		timeEnd = Stan.timeMaster.toLong();
		head = Stan.locationF10;
		status = "F10";
	}

	public void klet10()
	{	// полоса в клети
		// полученые усилия не равны расчетным
		// F5.t
		// F5.h_out
		// F6.setTerm
	}

	public void klet10End()
	{	// полоса вышла из клети
		timeEnd = Stan.timeMaster.toLong();
		calcHead(Stan.locationF10);
		status = "прошла F10";
	}

	// 13 полоса в толщиномере
	public void thickness()
	{
		t_out_sred += (stan.t4.sis()) ? stan.t4.getT() : stan.t5.getT();
		t_out_index++;
		h[index_h] = stan.thickness.h1;
		index_h++;
		h[index_h] = stan.thickness.h2;
		index_h++;
		h[index_h] = stan.thickness.h3;
		index_h++;
		h[index_h] = stan.thickness.h4;
		index_h++;
	}

	public void thicknessEnd()
	{	// полоса прошла толщиномер
		t_out_sred /= t_out_index;	// стедняя температура
		stan.t4.setTsred((short) t_out_sred);
		stan.t5.setTsred((short) t_out_sred);
		index_h = 0;
		timeEnd = Stan.timeMaster.toLong();
		if (stan.t4.sis())
			calcHead(Stan.locationThickness1);
		else
			calcHead(Stan.locationThickness2);
			calcHead(Stan.locationThickness2);
		status = "Прошла клети";
	}

	// данные о полосе передаются в текстовом формате
	// "strip"	номер строки	порядковый номер	толщина подката	ширина подката	длина подката
	// толщина полосы	ширина полосы	длина полосы	T1	T2	T3	Tout	T6
	// время поподания в T1	T2	T3	Tout	T6	Tend
	// "strip"	party.str_num	id	f4a_height	out_width	f4a_length
	// out_height	out_width	length	t1_sred	t2_sred	t3_sred	t_out_sred	t6_sred
	// timeStart	timeT2	timeT3	timeOut	timeT6	timeEnd
	// "strip"
	public String toString()
	{
		return "strip\t"+
				party.str_num+"\t"+id+"\t"+f4a_height+"\t"+out_width+"\t"+f4a_length+"\t" +
				out_height+"\t"+out_width+"\t"+getLength()+"\t"+
				t1_sred+"\t"+t2_sred+"\t"+t3_sred+"\t"+t_out_sred+"\t"+t6_sred +"\t"+
				timeStart+"\t"+timeT2+"\t"+timeT3+"\t"+timeOut+"\t"+timeT6+"\t"+timeEnd;
	}
	// !!!
	public void fromString(String in)
	{
		String array[]=in.split("\t");
		if (array != null && array.length == 20 && array[0].equals("strip"))
		{
			// !!!! array[1];	// номер строки
			id=Short.parseShort(array[2]);	// порядковый номер в партии
			f4a_height=Float.parseFloat(array[3]);	// толщина подката
			out_width=Float.parseFloat(array[4]);	// !!! ширина подката
			f4a_length=Float.parseFloat(array[5]);	// длина подката
			out_height=Float.parseFloat(array[6]);	// толщина полосы
			out_width=Float.parseFloat(array[7]);	// ширина полосы
			//!!!Float.parseFloat(array[8]);	// длина полосы
			t1_sred=Float.parseFloat(array[9]);		// средняя температура на 1 пироматре
			t2_sred=Float.parseFloat(array[9]);		// средняя температура на 2 пироматре
			t3_sred=Float.parseFloat(array[9]);		// средняя температура на 3 пироматре
			t_out_sred=Float.parseFloat(array[9]);	// средняя температура на 4 или 5 пироматре
			t6_sred=Float.parseFloat(array[9]);		// средняя температура на 6 пироматре

		}
	}

	public void save(OutputStream out)
	{
		try
		{
			out.write((toString() + '\n').getBytes());
		}
		catch (IOException e)
		{
			Logs.write("Error (" + toString() + ").save()");
		}
	}
}
