package lpc1700.stan;

import lpc1700.util.xml.XMLElement;
import lpc1700.util.xml.XMLInterface;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:39:10
 * Клеть чистовой группы
 * <p/>
 * настроить клеть (V скорость до клети,
 * Hi толщины до клети расчётные в предыдущей клети,
 * Ti температуры до клити расчётные в предыдущей клети,
 * толщина после)
 * {
 * определить растворы клети для каждого участка полосы
 * скорость полосы после клети
 * определить скорость клети
 * температры после клети для каждого участка полосы
 * }
 * от скорости движения зависит на сколко быстро будет остыват полоса а соотведственно изменятся плотности
 */
public class KletChistovaya extends Klet implements XMLInterface
{
	public short dr_pos;					// [10*micron] раствор на валки на стороне маш зала
	public boolean cool;					// охлождение
	public boolean work_scr_o;				// винт регулировки раствора клети работает
	public boolean work_scr_d;				// винт регулировки раствора клети работает

	public Lineika lineika;
	public Roll rollTop;
	public Roll rollTopWork;
	public Roll rollBottomWork;
	public Roll rollBottom;
	public double h_out;
	public double w;

	public KletChistovaya(String name)
	{
		super(name);
		cool = true;					// охлождение включено
		work_scr_o = true;				// винт регулировки раствора клети работает
		work_scr_d = true;				// винт регулировки раствора клети работает
		lineika = new Lineika();
		rollTop = new Roll();
		rollTopWork = new Roll();
		rollBottomWork = new Roll();
		rollBottom = new Roll();
	}

	// !!!
	public boolean sis()
	{
		return (op_force + dr_force) >= force_min;
	}

	/* !!!!! настройка клети (V скорость до клети,
	* Hi толщины до клети расчётные в предыдущей клети,
	* Ti температуры до клити расчётные в предыдущей клети)
	* известны толщина после клети h_out
	*/
	public void configuration(double v, double h, double t)
	{
		// скорость на выходе
		double v_out = v * h / h_out;
		/*
		DELTAH = H1 - H2
		RED = DELTAH / H1
         GLI = -0.0045 + RED * (0.3057 - 1.363 * (H2 / R))
            GLI = -0.0045 + RED * (0.3057 - 1.363 * (H2 * H1 * RED / ((TR.STR.STA(NC).SCH.HEAD.ARC)**2)))
!     Correct sliding with adaptation parameter
      GLI = TR.ADA.ADASLID(NC) * GLI
      TR.STR.ZON(NC+1).SCH.STRIP_SPEED = TR.STR.FLOW / H2
!     Set its scheduled speed to previously computed speed corrected with stand's sliding
      TR.STR.STA(NC).SCH.SPEED = TR.STR.ZON(NC+1).SCH.STRIP_SPEED / (1. + TR.STR.STA(NC).SCH.SLID)
		 
		 */

		// скорость клети m/c
		double v_klet = calcSpeed(v, h, h_out);
		// скорость клети обороты в минуту
		double v_motor = calcSpeedMotor(v_klet);
		// усилия прокатки
		double P = p_sr() * w * contactLength(h, h_out);
		// раствор клети
		// Изменение температуры
		// - при обжатии
		// - излучение
		// - конвенция
		// |- воздух

		// |- рольганг
		// |- клеть
		// ограничение по мощности двигателя
		// ограничения по прочности клети
		// ограничения по углу захвата
		// передать настройки следующей клети
		// next.configuration(next_v,next_h,next_t)
	}

	public void prokatka(double f)
	{
		// f усилия
		// сравниваем усилия расчетные и практические
		// если есть ошибка настроить модеть
		// передать новые настройки следующей клети
	}

	// изменение входной толщины
	public void setHeight(double h)
	{
		// configuration(v,h,t);
	}

	// изменение входной температуры
	public void setTerm(double t)
	{
		// configuration(v,h,t);
	}

	// ! диаметр рабочих валков
	private float diametrRoll()
	{
		return (rollTopWork.diametr + rollBottomWork.diametr) / 2;
	}

	// ! определение скорости клети [m/c]
	private double calcSpeed(double v, double h_in, double h_out)
	{
		return v * h_in / (h_in * 0.6 + h_out * 0.4);
	}

	// определение скорости мотора клети [оборотов в минуту]
	private double calcSpeedMotor(double v)
	{
		return v * 60 / (Math.PI * diametrRoll()) / motor.reductor;
	}

	// длина дуги контакта
	private double contactLength(double h_in, double h_out)
	{
		return Math.sqrt((h_out - h_in) * diametrRoll() / 2);
	}

	// ! среднее контактное давление
	private double p_sr()
	{
		return 0;
	}

	/*
	* !        Backup first value of contact arc
         ARC = TR.STR.STA(NC).SCH.CAL.ARC
!        Compute average value of metal flow stress
         KP = XKP(NC,TEMP,TR)
!        Compute geometrical factor
         QP = XQP(NC,TR)
!        Compute rolling force
         FOR = (KP * QP * W1 - TRAC) * ARC

	* */

	// !! снижюение температуры от действия гидрозбива

	private double termGidroZbiv(double h, double v)
	{
		return 500 / (h * v);
	}

	// !! прирост температуры вследствии пластической деформации
	private double termCompression()
	{
		return 0;
	}

	// !! снижение температуры в межклетевом промежутке
	private double termInterval()
	{
		return 0;
	}

	// !!!
	public void readFromXMLElement(XMLElement element)
	{
		if (element != null)
		{
			rollTop.readFromXMLElement(element.findElement("RollTop"));
			rollTopWork.readFromXMLElement(element.findElement("RollTopWork"));
			rollBottomWork.readFromXMLElement(element.findElement("RollBottomWork"));
			rollBottom.readFromXMLElement(element.findElement("RollBottom"));
		}
	}

	// !!!
	public XMLElement writeToXMLElement(String name)
	{
		XMLElement element = new XMLElement(name);
		element.addElement(rollTop.writeToXMLElement("RollTop"));
		element.addElement(rollTopWork.writeToXMLElement("RollTopWork"));
		element.addElement(rollBottomWork.writeToXMLElement("RollBottomWork"));
		element.addElement(rollBottom.writeToXMLElement("RollBottom"));
		return element;
	}
}
