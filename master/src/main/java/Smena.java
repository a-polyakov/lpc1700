/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 31.10.2007
 * Time: 8:53:00
 * Дата и смена
 */
public class Smena
{
	private short year;
	private byte month;
	private byte day;
	private byte smen;
	private byte hour;
	private byte min;
	private byte sec;
	private short msec;
	public static final String nameMonth[] = new String[]{
			"01 Январь", "02 Февраль", "03 Март",
			"04 Апрель", "05 Май", "06 Июнь",
			"07 Июль", "08 Август", "09 Сентябрь",
			"10 Октябрь", "11 Ноябрь", "12 Декабрь"};

	public Smena()
	{
		long l = System.currentTimeMillis();
		set(l + 7200000); // часовой пояс +2ч=2*60*60*1000
	}

	public Smena(long l)
	{
		set(l + 7200000); // часовой пояс +2ч=2*60*60*1000
	}

	public void set(long l)
	{
		msec = (short) (l % 1000);
		l = l / 1000;
		sec = (byte) (l % 60);
		l = l / 60;
		min = (byte) (l % 60);
		l = l / 60;
		hour = (byte) (l % 24);
		smen = (byte) (hour / 8 + 1);
		l = l / 24; // дней с 1.1.1970
		year = (short) (l * 4 / 1461 + 1970);
		l = l % 1461;
		l = l % 365;
		int i;
		month = 1;
		for (i = 0; i < 12 && l - getDaysInMonth() > 0; i++)
		{
			l -= getDaysInMonth();
			month++;
		}
		day = (byte) (l % getDaysInMonth() + 1);
	}

	// "11:14"
	public void setTime(String s)
	{
		String arrayString[] = s.split(":");
		if (arrayString != null)
		{
			byte temp;
			if (arrayString.length >= 2)
			{	// HH:MM
				temp = Byte.parseByte(arrayString[0]);
				setHour(temp);
				temp = Byte.parseByte(arrayString[1]);
				setMin(temp);
				if (arrayString.length == 3)
				{	// HH:MM:SS
					temp = Byte.parseByte(arrayString[1]);
					setSec(temp);
				}
			}
		}
	}

	public String toStringHHMMSS()
	{
		return  toStringHH() + ":" +
				toStringMM() + ":" +
				toStringSS();
	}

	public String toStringHH()
	{
		return (hour > 9) ? String.valueOf(hour) : "0" + hour;
	}
	public String toStringMM()
	{
		return (min > 9) ? String.valueOf(min) : "0" + min;
	}
	public String toStringSS()
	{
		return (sec > 9) ? String.valueOf(sec) : "0" + sec;
	}
	public String toStringmsec()
	{
		return (msec > 90) ? String.valueOf(msec) : (msec > 9) ? "0" + msec : "00" + msec;
	}

	public String toStringDateTime()
	{
		return year + "."+
				((month > 9) ? month : "0" + month) + "."+
				((day > 9) ? day : "0" + day) + " "+
				toStringTime();
	}
	public String toStringTime()
	{
		return toStringHHMMSS()+":"+
				toStringmsec();
	}

	public long toLong()
	{
		long l = msec + sec * 1000l + min * 60000l + hour * 3600000l - 7200000l + (day - 1) * 86400000l; // часовой пояс +2ч=2*60*60*1000
		int i;
		for (i = 1; i < month; i++)
			l += getDaysInMonth(i) * 86400000l;
		l += (year / 4 - 492) * 1461 * 86400000l;
		l += (year % 4 - 2) * 365 * 86400000l;
		return l;
	}

	public short getYear()
	{
		return year;
	}

	public void setYear(short year)
	{
		if (year > 0)
			this.year = year;
	}

	public void incYear()
	{
		year++;
	}

	public void decYear()
	{
		year--;
	}

	public boolean higYear()
	{
		return year % 4 == 0;
	}


	public byte getMonth()
	{
		return month;
	}

	public String getNameMonth()
	{
		return nameMonth[month - 1];
	}

	public void setMonth(byte month)
	{
		if (month > 0 && month < 13)
		{
			this.month = month;
			if (day > getDaysInMonth())
				day = getDaysInMonth();
		}
	}

	public void incMonth()
	{
		if (month == 12)
		{
			incYear();
			month = 1;
		} else
			month++;
	}

	public void decMonth()
	{
		if (month == 1)
		{
			decYear();
			month = 12;
		} else
			month--;
	}

	public byte getDaysInMonth()
	{
		switch (month)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				return 31;
			case 2:
				if (higYear())
					return 29;
				else
					return 28;
			case 4:
			case 6:
			case 9:
			case 11:
				return 30;
			default:
				return 0;
		}
	}

	public byte getDaysInMonth(int month)
	{
		switch (month)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				return 31;
			case 2:
				if (higYear())
					return 29;
				else
					return 28;
			case 4:
			case 6:
			case 9:
			case 11:
				return 30;
			default:
				return 0;
		}
	}

	public byte getDay()
	{
		return day;
	}

	public void setDay(byte day)
	{
		if (day > 0 && day <= getDaysInMonth())
			this.day = day;
	}

	public void incDay()
	{
		if (day == getDaysInMonth())
		{
			incMonth();
			day = 1;
		} else
			day++;
	}

	public void decDay()
	{
		if (day == 1)
		{
			decMonth();
			day = getDaysInMonth();
		} else
			day--;
	}


	public byte getSmen()
	{
		return smen;
	}

	public void setSmen(byte smen)
	{
		if (smen > 0 && smen < 4)
			this.smen = smen;
	}

	// следующяя смена
	public void incSmen()
	{
		if (smen == 3)
		{
			incDay();
			smen = 1;
		} else
			smen++;
	}

	// предыдущяя смена
	public void decSmen()
	{
		if (smen == 1)
		{
			decDay();
			smen = 3;
		} else
			smen--;
	}

	public byte getHour()
	{
		return hour;
	}

	public void setHour(byte hour)
	{
		if (hour >= 0 && hour < 24)
			this.hour = hour;
	}

	public byte getMin()
	{
		return min;
	}

	public void setMin(byte min)
	{
		if (min >= 0 && min < 60)
			this.min = min;
	}

	public byte getSec()
	{
		return sec;
	}

	public void setSec(byte sec)
	{
		if (sec >= 0 && sec < 60)
			this.sec = sec;
	}

	public short getMsec()
	{
		return msec;
	}

	public void setMsec(short msec)
	{
		if (msec >= 0 && msec < 1000)
			this.msec = msec;
	}
}
