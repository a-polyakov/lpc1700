import java.util.Arrays;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 05.10.2007
 * Time: 7:32:25
 * Партия слябов
 *
 * * неправильное считывание float
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

	public Party()
	{
		str_num = 0;					// номер строки (сквозная нумерация за смену)
		melt = "new";					// плавка
		serial = 0;						// серия (нумерация для каждой плавки)
		time_enter = 0;					// ввремя ввода
		nakladnaya = "new";				// ! накладная
		customer = "new";				// ! заказчик
		uchastok = "new";				// ! участок
		grade = "new";					// марка стали
		in_height = 0.13f;		// толщина сляба, м
		in_width = 0.8f;		// ширина сляба, м
		in_length = 6;	// ! длина сляба, м
		f4a_height = 0.025f;			// толщина подката, м
		out_height = 0.008f;	// толщина полосы, м
		out_width = 1;	// ширина полосы, м
		strip_in = 0;					// штук в партии
		strip_under = 0;				// штук недокатано
		strip_throw = 0;				// штук выброшено
		strip_out = 0;					// штук прокатано
	}

	// данные о партии передаются в текстовом формате
	// "party"	номер строки	плавка	серия	ввремя ввода	марка стали	сляб толщина	ширина	длина	толщина подката
	// толщина полосы	ширина полосы	штук в партии	штук недокатано	штук выброшено	штук прокатано
	// "party"	str_num	melt	serial	time_enter	grade	in_height	in_width	in_length	f4a_height
	// out_height	out_width	strip_in	strip_under	strip_throw	strip_out
	// "party\t1\t76642/1\t1\t11:09\t81 - 08KP\t0.175\t0.799\t6.2\t8.1\t0.025\t0.002\t1.05\t10\t0\t0\t2"
	// считать данные о партии
	public void StringToParty(String in)
	{
		String arrayString[] = in.split("\t");
		if (arrayString != null && arrayString.length == 17 && arrayString[0].equals("party"))
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
			f4a_height = Float.parseFloat(arrayString[10]);		// толщина подката, м
			out_height = Float.parseFloat(arrayString[11]);		// толщина полосы, м
			out_width = Float.parseFloat(arrayString[12]);		// ширина полосы, м
			strip_in = Short.parseShort(arrayString[13]);		// штук в партии
			strip_under = Short.parseShort(arrayString[14]);	// штук недокатано
			strip_throw = Short.parseShort(arrayString[15]);	// штук выброшено
			strip_out = Short.parseShort(arrayString[16]);		// штук прокатано
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

	public static void readPdi(byte in[], Vector vector)
	{
		Party party;
		int i;
		byte serial[];
		byte str_num[];
		byte grade[];
		int pr_sr;
		float cs_thi, cs_weight, cro_thi, cstr_thi, cstr_wid;
		for (i = 0; i < 7; i++)
		{
			serial = Arrays.copyOfRange(in, 52*i+0, 52*i+8);					// char SERIAL_NO[10]
			str_num = Arrays.copyOfRange(in, 52*i+10, 52*i+13);					// char XEN_STR_NUM[3]
																				// char XEN_SHIFT[2]
			grade = Arrays.copyOfRange(in, 52*i+15, 52*i+24);					// char GRADE[9]
			pr_sr = readShort(Arrays.copyOfRange(in, 52*i+24, 52*i+26));		// short int NB_PR_SR
																				// short int NB_PR_MIL
																				// short int ANALOG
			cs_thi = readFloat(Arrays.copyOfRange(in, 52*i+32, 52*i+36));		// CS_THI толщина сляба
			cs_weight = readFloat(Arrays.copyOfRange(in, 52*i+36, 52*i+40))*100;// CS_WEIGHT *100 ширина сляба
			cro_thi = readFloat(Arrays.copyOfRange(in, 52*i+40, 52*i+44));		// CRO_THI толщина подката
			cstr_thi = readFloat(Arrays.copyOfRange(in, 52*i+44, 52*i+48));		// CSTR_THI толщина полосы
			cstr_wid = readFloat(Arrays.copyOfRange(in, 52*i+48, 52*i+52));		// CSTR_WID ширина полосы

			party =new Party();
			party.melt=new String(serial).trim();
			party.serial = (short) (in[52*i+8]-'0');
			party.str_num=Short.parseShort(new String(str_num).trim());
			party.grade=new String(grade).trim();
			party.strip_in=(short)pr_sr;
			party.in_height=cs_thi/1000;
			party.in_width=cs_weight/1000;
			party.f4a_height=cro_thi/1000;
			party.out_height=cstr_thi/1000;
			party.out_width=cstr_wid/1000;
			party.time_enter=System.currentTimeMillis();
			if (!vector.contains(party))
				vector.addElement(party);
		}
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

	private static int readShort(byte in[])
	{
		int ch1 = (in[0]<0)?256+in[0]:in[0];
		int ch2 = (in[1]<0)?256+in[1]:in[1];
		return ((ch2 << 8) + ch1);
	}

	private static float readFloat(byte in[])
	{
		int ch1 = (in[0]<0)?256+in[0]:in[0];
		int ch2 = (in[1]<0)?256+in[1]:in[1];
		int ch3 = (in[2]<0)?256+in[2]:in[2];
		int ch4 = (in[3]<0)?256+in[3]:in[3];
		return Float.intBitsToFloat((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1);
	}
}