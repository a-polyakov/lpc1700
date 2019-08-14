package lpc1700.stan;

import lpc1700.Logs;
import lpc1700.Smena;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 05.10.2007
 * Time: 7:32:25
 * Партия слябов
 */
public class Party
{
	public short str_num;		// номер строки (сквозная нумерация за смену)
	public String melt;			// плавка
	public short serial;		// серия (нумерация для каждой плавки)
	public long time_enter;		// ввремя ввода
	String nakladnaya;	// ! накладная
	String customer;	// ! заказчик
	String uchastok;	// ! участок
	public String grade;		// марка стали
	public float in_height;		// толщина сляба, м
	public float in_width;		// ширина сляба, м
	public float in_length;		// длина сляба, м
	public float f4a_height;	// толщина подката, м
	public float out_height;	// толщина полосы, м
	public float out_width;		// ширина полосы, м
	public short strip_in;		// штук в партии
	public short strip_under;	// штук недокатано
	public short strip_throw;	// штук выброшено
	private short strip_out;	// штук прокатано

	private byte posProcent[];	//

	public Party(byte posProcent[])
	{
		str_num = 0;					// номер строки (сквозная нумерация за смену)
		melt = "new";					// плавка
		serial = 0;						// серия (нумерация для каждой плавки)
		time_enter = 0;					// ввремя ввода
		nakladnaya = "new";				// ! накладная
		customer = "new";				// ! заказчик
		uchastok = "new";				// ! участок
		grade = "new";					// марка стали
		in_height = Strip.H_IN_MAX;		// толщина сляба, м
		in_width = Strip.W_IN_MAX;		// ширина сляба, м
		in_length = Strip.L_IN_MAX[2];	// ! длина сляба, м
		f4a_height = 0.025f;			// толщина подката, м
		out_height = Strip.H_OUT_MAX;	// толщина полосы, м
		out_width = Strip.W_OUT_MAX;	// ширина полосы, м
		strip_in = 0;					// штук в партии
		strip_under = 0;				// штук недокатано
		strip_throw = 0;				// штук выброшено
		strip_out = 0;					// штук прокатано
		setPosProcent(posProcent);
	}

	public Party(InputStream in) throws IOException
	{
		byte bufer[]=new byte[512];
		in.read(bufer);
		String s=new String(bufer);
		fromString(s);
	}

	// установить процент обжатия
	public void setPosProcent(byte posProcent[])
	{
		if (posProcent.length == 6 &&
				posProcent[0] + posProcent[1] + posProcent[2] + posProcent[3] + posProcent[4] + posProcent[5] == 100)
		{
			this.posProcent = posProcent;
		}
	}

	public byte[] getPosProcent()
	{
		return posProcent;
	}

	// данные о партии передаются в текстовом формате
	// "party"	номер строки	плавка	серия	ввремя ввода	марка стали	сляб толщина	ширина	длина	толщина подката
	// толщина полосы	ширина полосы	штук в партии	штук недокатано	штук выброшено	штук прокатано
	// "party"	str_num	melt	serial	time_enter	grade	in_height	in_width	in_length	f4a_height
	// out_height	out_width	strip_in	strip_under	strip_throw	strip_out
	// "party\t1\t76642/1\t1\t11:09\t81 - 08KP\t0.175\t0.799\t6.2\t8.1\t0.025\t0.002\t1.05\t10\t0\t0\t2"
	// считать данные о партии
	public void fromString(String in)
	{
		String arrayString[] = in.split("\t");
		if (arrayString != null && arrayString.length == 16 && arrayString[0].equals("party"))
		{
			str_num = Short.parseShort(arrayString[1]);			// номер строки (сквозная нумерация за смену)
			melt = arrayString[2];								// плавка
			serial = Short.parseShort(arrayString[3]);			// серия (нумерация для каждой плавки)
			Smena time = new Smena();
			time.setTime(arrayString[4]);
			time_enter = time.toLong();							// ввремя ввода
			nakladnaya = "new";				// !!! накладная
			customer = "new";				// !!! заказчик
			uchastok = "new";				// !!! участок
			grade = arrayString[5];								// марка стали
			in_height = Float.parseFloat(arrayString[6]);		// толщина сляба, м
			in_width = Float.parseFloat(arrayString[7]);		// ширина сляба, м
			in_length = Float.parseFloat(arrayString[8]);		// длина сляба, м
			f4a_height = Float.parseFloat(arrayString[9]);		// толщина подката, м
			out_height = Float.parseFloat(arrayString[10]);		// толщина полосы, м
			out_width = Float.parseFloat(arrayString[11]);		// ширина полосы, м
			strip_in = Short.parseShort(arrayString[12]);		// штук в партии
			strip_under = Short.parseShort(arrayString[13]);	// штук недокатано
			strip_throw = Short.parseShort(arrayString[14]);	// штук выброшено
			strip_out = Short.parseShort(arrayString[15]);		// штук прокатано
		}
	}

	// ! записать данные о партии
	public String toString()
	{
		String out;
		out = "party\t" + str_num + '\t'
				+ melt + '\t'
				+ serial + '\t'
				+ new Smena(time_enter).toStringHHMMSS() + '\t'
				+ grade + '\t'
				+ in_height + '\t'
				+ in_width + '\t'
				+ in_length + '\t'
				+ f4a_height + '\t'
				+ out_height + '\t'
				+ out_width + '\t'
				+ strip_in + '\t'
				+ strip_under + '\t'
				+ strip_throw + '\t'
				+ strip_out;
		return out;
	}

	// ещё одна штука партии
	public void setNextStrip(Strip strip)
	{
		strip.setParty(this);
		strip_out++;
	}

	// количество прокатаных штук
	public int getEndStrips()
	{
		return strip_out + strip_throw + strip_under;
	}
	public int getOutStrip()
	{
		return strip_out;
	}

	// все полосы партии прокатаны
	public void setEndStrips()
	{
		strip_in = (short) (strip_out + strip_throw + strip_under);
	}

	// партия прокатана
	public boolean finish()
	{
		return strip_out + strip_throw + strip_under >= strip_in;
	}

	// проверка на равенство партий
	public boolean equals(Object o)
	{
		if (o != null && o.getClass() == Party.class)
		{
			String s = ((Party) o).melt;
			short i = ((Party) o).serial;
			if ((melt == null || melt.equals("")) && (s == null || s.equals("")) && (serial == i))
				return true;
			if (melt != null && s != null && melt.equals(s) && serial == i)
				return true;
		}
		return false;
	}

	public Party clone()
	{
		Party temp = new Party(posProcent);
		temp.str_num = str_num;			// номер строки (сквозная нумерация за смену)
		temp.melt = melt;				// плавка
		temp.serial = serial;			// серия (нумерация для каждой плавки)
		temp.time_enter = time_enter;	// ввремя ввода
		temp.nakladnaya = nakladnaya;	// накладная
		temp.customer = customer;		// заказчик
		temp.uchastok = uchastok;		// участок
		temp.grade = grade;				// марка стали
		temp.in_height = in_height;		// толщина сляба, м
		temp.in_width = in_width;		// ширина сляба, м
		temp.in_length = in_length;		// длина сляба, м
		temp.f4a_height = f4a_height;	// толщина подката, м
		temp.out_height = out_height;	// толщина полосы, м
		temp.out_width = out_width;		// ширина полосы, м
		temp.strip_in = strip_in;		// штук в партии
		temp.strip_under = strip_under;	// штук недокатано
		temp.strip_throw = strip_throw;	// штук выброшено
		temp.strip_out = strip_out;		// штук прокатано
		return temp;
	}

	// !!! сохранение сведений о партии
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
