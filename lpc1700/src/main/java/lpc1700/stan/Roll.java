package lpc1700.stan;

import lpc1700.util.xml.XMLAttribute;
import lpc1700.util.xml.XMLElement;
import lpc1700.util.xml.XMLInterface;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 26.09.2007
 * Time: 13:34:05
 * Валок
 * <p/>
 * диаметр, мм
 * рабочие
 * черновой окалиноломатель	900..810
 * 1						1020..900
 * 2						1450..1250
 * 3-4а						880..810
 * 5,6						670..625
 * 7-10						705..655
 * опорные
 * 1						1450-1370
 * 2						1450-1250
 * 3						1300-1170
 * 4						1300-1184
 * 4а						1300-1170
 * 5-10						1325-1220
 * <p/>
 * периот перевалки
 * рабочий
 * черновой окалиноломатиль	120 000 т
 * 1,2						60 000 т
 * 3						24 000 т
 * 4,4а						27 000 т
 * 5-10						140..170 км
 * опорные
 * 1-4						240 000 т
 * 4а						180 000 т
 * 5-10						3 раза в месяц
 * вертикальные
 * 2-4						1 000 000 т
 * запрещяется использавать в чистовой группе валков с твердостью менее 71 единиц по Шору
 * твердость парных волков не должна отличаться болеее чем на 5 единиц по Шору
 * <p/>
 * допустимые разхождение диаметров валков
 * опорные
 * рабочие
 * окалиноломатель	4..8 мм
 * 1-4а				4..8 мм
 * 5-9				0,3 мм
 * 10					верхний > 0,3
 */
public class Roll implements XMLInterface
{
	// диаметры опорных валков
	public static float ROLL_F1F2_OPOR_MAX = 1.450f;
	public static float ROLL_F1_OPOR_MIN = 1.370f;
	public static float ROLL_F2_OPOR_MIN = 1.250f;
	public static float ROLL_F3F4a_OPOR_MAX = 1.3f;
	public static float ROLL_F3_OPOR_MIN = 1.17f;
	public static float ROLL_F4_OPOR_MIN = 1.184f;
	public static float ROLL_F4a_OPOR_MIN = 1.17f;
	public static float ROLL_F5F10_OPOR_MAX = 1.325f;
	public static float ROLL_F5F10_OPOR_MIN = 1.220f;
	// диаметры рабочих валков
	public static float ROLL_F0_WORK_MAX = 0.900f;
	public static float ROLL_F0_WORK_MIN = 0.81f;
	public static float ROLL_F1_WORK_MAX = 1.02f;
	public static float ROLL_F1_WORK_MIN = 0.9f;
	public static float ROLL_F2_WORK_MAX = 1.45f;
	public static float ROLL_F2_WORK_MIN = 1.25f;
	public static float ROLL_F3F4a_WORK_MAX = 0.880f;
	public static float ROLL_F3F4a_WORK_MIN = 0.81f;
	public static float ROLL_F5F6_WORK_MAX = 0.670f;
	public static float ROLL_F5F6_WORK_MIN = 0.625f;
	public static float ROLL_F7F10_WORK_MAX = 0.705f;
	public static float ROLL_F7F10_WORK_MIN = 0.655f;

	public long time;		// время установки валка
	public int nb;			// !!! номер
	public float diametr;	// начальный диаметр
	public float crown;		// бочкообразность
	public float d_max;		// максимальный диаметр
	public float d_min;		// минимальный диаметр

	public Roll()
	{
		time = System.currentTimeMillis();
		nb = 0;
		diametr = 0.690f;
		crown = 0;
	}

	// !!!
	public void readFromXMLElement(XMLElement element)
	{
		if (element != null)
			try
			{
				nb = Integer.parseInt(element.getAttribute("nb").getValue());
				diametr = Float.parseFloat(element.getAttribute("diametr").getValue());
				crown = Float.parseFloat(element.getAttribute("crown").getValue());
			}
			catch (Exception e)
			{
			}
	}

	// !!!
	public XMLElement writeToXMLElement(String name)
	{
		XMLElement element = new XMLElement(name);
		element.addAttribute(new XMLAttribute("nb", String.valueOf(nb)));
		element.addAttribute(new XMLAttribute("diametr", String.valueOf(diametr)));
		element.addAttribute(new XMLAttribute("crown", String.valueOf(crown)));
		return element;
	}
}
