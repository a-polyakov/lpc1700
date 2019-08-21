package lpc1700.stan;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 12.09.2007
 * Time: 14:55:05
 * Толщиномер
 */
public class Thickness
{
	public short h1, h2, h3, h4;
	public Strip strip;
	private int indexArray;
	private short array[];
	private boolean sis;

	public Thickness()
	{
		h1 = 0;
		h2 = 0;
		h3 = 0;
		h4 = 0;
		strip = null;
		array = new short[1000];
		for (indexArray = array.length - 1; indexArray > 0; indexArray--)
			array[indexArray] = (short)(65*indexArray-32500);
		indexArray=500;
		sis = false;
	}

	public void setH()
	{
		int h = h1 + h2 + h3 + h4;
		h = h / 4;
		if (h < -30000)
			sis = false;
		else
		{
			if (sis)
			{
					array[indexArray] = (short)h;
					indexArray++;
					if (indexArray >= array.length)
						indexArray = 0;
			}
		}
	}

	public void start()
	{
		for (; indexArray > 0; indexArray--)
			array[indexArray] = -32500;
		sis = true;
	}

	public short[] getH()
	{
		return Arrays.copyOf(array,indexArray);
	}
}
