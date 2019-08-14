package lpc1700.stan;

import lpc1700.util.xml.XMLAttribute;
import lpc1700.util.xml.XMLElement;
import lpc1700.util.xml.XMLInterface;

import java.util.Arrays;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 28.11.2007
 * Time: 10:41:44
 * процент обжатия для клети чистовой группы
 */
public class PosProcent implements XMLInterface
{
	private Vector procent;					// стандартные схемы распределения нагрузок
	private int indexProcent;

	public PosProcent()
	{
		procent = new Vector();
		indexProcent = 0;				// выбрана первая из списка
	}

	// выбрать процент обжатия true новая запись false старая
	public boolean setPosProcent(byte arrayByte[])
	{
		int i;
		byte temp[];
		boolean find = false;
		for (i = 0; !find && i < procent.size(); i++)
		{
			temp = (byte[]) procent.elementAt(i);
			if (Arrays.equals(arrayByte, temp))
				find = true;
		}
		if (!find)
		{	// добавить и выбрать новое растределение
			procent.addElement(arrayByte);
			indexProcent = procent.indexOf(arrayByte);
		} else
		{	// выбрать
			indexProcent = i;
		}
		return !find;
	}

	// выбрать процент обжатия
	public void setPosProcent(int index)
	{
		if (index >= 0 && index < procent.size())
			indexProcent = index;
	}

	// текущий процынт обжатия
	public byte[] getPosProcent()
	{
		return (byte[]) procent.elementAt(indexProcent);
	}

	public byte[][] getListPosProcent()
	{
		byte temp[][];
		Object object[] = procent.toArray();
		temp = new byte[object.length][];
		int i;
		for (i = 0; i < object.length; i++)
			temp[i] = (byte[]) object[i];
		return temp;
	}

	public void init()
	{
		procent.addElement(new byte[]{16, 18, 17, 17, 16, 16});
		procent.addElement(new byte[]{15, 19, 18, 17, 16, 15});
	}

	// загрузить таблицу распределения нагрузок
	public XMLElement writeToXMLElement(String name)
	{
		XMLElement element = new XMLElement(name);
		XMLElement temp;
		int i;
		byte arrayByte[];
		for (i = 0; i < procent.size(); i++)
		{
			arrayByte = (byte[]) procent.elementAt(i);
			temp = new XMLElement("Value");
			temp.addAttribute(new XMLAttribute("id", String.valueOf(i)));
			temp.addAttribute(new XMLAttribute("f5", String.valueOf(arrayByte[0])));
			temp.addAttribute(new XMLAttribute("f6", String.valueOf(arrayByte[1])));
			temp.addAttribute(new XMLAttribute("f7", String.valueOf(arrayByte[2])));
			temp.addAttribute(new XMLAttribute("f8", String.valueOf(arrayByte[3])));
			temp.addAttribute(new XMLAttribute("f9", String.valueOf(arrayByte[4])));
			temp.addAttribute(new XMLAttribute("f10", String.valueOf(arrayByte[5])));
			element.addElement(temp);
		}
		temp = new XMLElement("Check");
		temp.addAttribute(new XMLAttribute("id", String.valueOf(indexProcent)));
		element.addElement(temp);
		return element;
	}

	// !!! загрузить таблицу распределения нагрузок
	public void readFromXMLElement(XMLElement element)
	{
		if (element != null)
		{
			int l = element.getCountElements();
			if (l > 0)
			{
				int id;
				byte arrayByte[];
				XMLElement currentElement;
				for (int i = 0; i < l; i++)
				{
					currentElement = element.getElement(i);
					if ("Value".equals(currentElement.getName()))
					{
						id = Integer.parseInt(currentElement.getAttribute("id").getValue());
						arrayByte = new byte[6];
						arrayByte[0] = Byte.parseByte(currentElement.getAttribute("f5").getValue());
						arrayByte[1] = Byte.parseByte(currentElement.getAttribute("f6").getValue());
						arrayByte[2] = Byte.parseByte(currentElement.getAttribute("f7").getValue());
						arrayByte[3] = Byte.parseByte(currentElement.getAttribute("f8").getValue());
						arrayByte[4] = Byte.parseByte(currentElement.getAttribute("f9").getValue());
						arrayByte[5] = Byte.parseByte(currentElement.getAttribute("f10").getValue());
						procent.insertElementAt(arrayByte, id);
					}
				}
				currentElement = element.findElement("Check");
				if (currentElement != null)
				{
					XMLAttribute currentAttribute = currentElement.getAttribute("id");
					if (currentAttribute != null)
						indexProcent = Integer.parseInt(currentAttribute.getValue());
					else
						indexProcent = 0;
				} else
					indexProcent = 0;
			} else
				init();
		} else
			init();
	}
}
