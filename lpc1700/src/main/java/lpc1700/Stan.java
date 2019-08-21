package lpc1700;

import lpc1700.stan.*;
import lpc1700.util.xml.XMLAttribute;
import lpc1700.util.xml.XMLElement;
import lpc1700.util.xml.XMLInterface;
import lpc1700.util.xml.XMLPage;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:48:49
 * Стан
 * <p/>
 * скорость прокатки m/c (можно вычислять зная скорость двигателя и диаметр валков)
 * печной рольганг			1,6
 * черновой окаленоломатель	0,84(0.99)
 * рольганг					1,6
 * 1						1,27(1.25)
 * рольганг					1,6
 * 2						1,35(1.25)
 * рольганг					2,5
 * 3						1,74(2.27)
 * рольганг					2,5
 * 4						1,74(2.27)
 * рольганг					3,2
 * 4а						2,9(2.8)
 * промежуточный рольганг	1,6-3,5
 * гидрозбив				0.7-1.7
 * 5						1,32-2,5
 * 6						1,9-3,6
 * 7,8						3,75-8,15
 * 9						5,95-11,2
 * 10						5,95-12,5(12)
 * отводящий рольганг		3-12
 * <p/>
 * номинальные скорости для прокатки
 * 2	550
 * 2.3	540
 * 2.5	520
 * 2.8	510
 * 3	500
 * 3.2	480
 * 3.5	450
 * 3.8	440
 * 4	420
 * 4.5	380
 * 5	350
 * 6	320
 * 7	280
 * 8	280
 * >8	200
 * <p/>
 * направляющие линейки устанавливаются на 100 мм шире номинальной ширины для
 * черновой группы и на 70 мм для чистовой
 * <p/>
 * температура перед чистовой группой должна быть не менее 980 С
 * нагрузка на двигатели не более 8 кА (15кА мгновенная)
 * <p/>
 * хранить данные о партиях при вводе новой партии следующего дня сохранять предыдущие в файл
 * сохранение партий полос по сменно и при закрытии программы
 * <p/>
 * износ валков
 * ограничения по углу захвата, по мощности двигателя, по прочности прокатных валков
 * <p/>
 * уточнить растояния
 * ложные сигналы sis:
 * - t4 t5
 * <p/>
 * имена элементов определить константами
 */
public class Stan implements XMLInterface
{
	// место расположение элементов
	public static float locationF_0 = 0;
	public static float locationF_1 = locationF_0 + 10.15f;
	public static float locationF_2 = locationF_1 + 18.5f;
	public static float locationF_3 = locationF_2 + 22.98f;
	public static float locationF_4 = locationF_3 + 35.42f;
	public static float locationF4a = locationF_4 + 53;
	public static float locationT1 = locationF4a + 5;				// !
	public static float locationNognici = locationF4a + 87.8f;
	public static float locationT2 = locationNognici - 3;			// !
	public static float locationGidrozbiv = locationNognici + 12;
	public static float locationT3 = locationNognici + 3;			// !
	public static float locationF_5 = locationGidrozbiv + 6;
	public static float locationF_6 = locationF_5 + 6;
	public static float locationF_7 = locationF_6 + 5.791f;
	public static float locationF_8 = locationF_7 + 5.791f;
	public static float locationF_9 = locationF_8 + 5.791f;
	public static float locationF10 = locationF_9 + 5.791f;
	public static float locationThickness1 = locationF10 + 3.860f;
	public static float locationT4 = locationThickness1;
	public static float locationThickness2 = locationThickness1 + 0.920f;
	public static float locationT5 = locationThickness2;
	public static float locationWind_1 = locationF10 + 108;
	public static float locationT6 = locationWind_1 - 3;			// !
	public static float locationWind_2 = locationWind_1 + 5;		// !
	public static float locationWind_3 = locationWind_2 + 5;		// !

	public static String NAME_KLET_0 = "F0";
	public static String NAME_KLET_1 = "F1";
	public static String NAME_KLET_2 = "F2";
	public static String NAME_KLET_3 = "F3";
	public static String NAME_KLET_4 = "F4";
	public static String NAME_KLET_4a = "F4a";
	public static String NAME_KLET_5 = "F5";
	public static String NAME_KLET_6 = "F6";
	public static String NAME_KLET_7 = "F7";
	public static String NAME_KLET_8 = "F8";
	public static String NAME_KLET_9 = "F9";
	public static String NAME_KLET_10 = "F10";

	public boolean pech1, pech2, pech3, tranzit, start;
	// черновая группа клетей
	public Klet klet0, klet1, klet2, klet3, klet4, klet4a;
	public Pyrometer t1;					// пирометр на выходе из черновой группы клетей
	// рольганг со сталкивателем
	public Nognici nognici;					// ножницы
	public Pyrometer t2;					// пирометр на ножницах
	Gidrozbiv gidrozbiv;					// гидрозбив
	public Pyrometer t3;					// пирометр перед чистовой группой клетей
	// чистовая группа клетей
	public KletChistovaya F5;				//
	public Loop loop5_6;					//
	public KletChistovaya F6;				//
	public Loop loop6_7;					//
	public KletChistovaya F7;				//
	public Loop loop7_8;					//
	public KletHidro F8;					// гидравлические клети
	public Loop loop8_9;					//
	public KletHidro F9;					//
	public Loop loop9_10;					//
	public KletHidro F10;					//
	public Thickness thickness;				// толщиномер
	public Pyrometer t4, t5;				// пирометры на толщиномерах
	public Pyrometer t6;					// пирометр перед моталками
	public Wind wind_1, wind_2, wind_3;		// моталки

	public Parties parties;					// список партий за смену
	public Strips strips;					// список прокатаных полос за смену
	public Grades grades;					// таблица марок стали

	public short temp;			///!!!!

	public PosProcent posProcent;			// схемы распределения нагрузок

	public static Smena timeMaster = new Smena();

	public Stan()
	{
		klet0 = new Klet(NAME_KLET_0);
		klet1 = new Klet(NAME_KLET_1);
		klet2 = new Klet(NAME_KLET_2);
		klet3 = new Klet(NAME_KLET_3);
		klet4 = new Klet(NAME_KLET_4);
		klet4a = new Klet(NAME_KLET_4a);
		t1 = new Pyrometer(850, 1200);
		t2 = new Pyrometer(850, 1200);
		nognici = new Nognici();
		t3 = new Pyrometer(850, 1200);
		gidrozbiv = new Gidrozbiv();
		F5 = new KletChistovaya(NAME_KLET_5);
		F5.motor.reductor = 0.22f;
		F5.rollTop.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F5.rollTop.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		F5.rollTopWork.d_max = Roll.ROLL_F5F6_WORK_MAX;
		F5.rollTopWork.d_min = Roll.ROLL_F5F6_WORK_MIN;
		F5.rollBottomWork.d_max = Roll.ROLL_F5F6_WORK_MAX;
		F5.rollBottomWork.d_min = Roll.ROLL_F5F6_WORK_MIN;
		F5.rollBottom.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F5.rollBottom.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		loop5_6 = new Loop();
		F6 = new KletChistovaya(NAME_KLET_6);
		F6.motor.reductor = 0.32f;
		F6.rollTop.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F6.rollTop.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		F6.rollTopWork.d_max = Roll.ROLL_F5F6_WORK_MAX;
		F6.rollTopWork.d_min = Roll.ROLL_F5F6_WORK_MIN;
		F6.rollBottomWork.d_max = Roll.ROLL_F5F6_WORK_MAX;
		F6.rollBottomWork.d_min = Roll.ROLL_F5F6_WORK_MIN;
		F6.rollBottom.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F6.rollBottom.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		loop6_7 = new Loop();
		F7 = new KletChistovaya(NAME_KLET_7);
		F7.rollTop.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F7.rollTop.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		F7.rollTopWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F7.rollTopWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F7.rollBottomWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F7.rollBottomWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F7.rollBottom.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F7.rollBottom.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		loop7_8 = new Loop();
		F8 = new KletHidro(NAME_KLET_8);
		F8.rollTop.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F8.rollTop.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		F8.rollTopWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F8.rollTopWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F8.rollBottomWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F8.rollBottomWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F8.rollBottom.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F8.rollBottom.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		loop8_9 = new Loop();
		F9 = new KletHidro(NAME_KLET_9);
		F9.rollTop.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F9.rollTop.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		F9.rollTopWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F9.rollTopWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F9.rollBottomWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F9.rollBottomWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F9.rollBottom.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F9.rollBottom.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		loop9_10 = new Loop();
		F10 = new KletHidro(NAME_KLET_10);
		F10.rollTop.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F10.rollTop.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		F10.rollTopWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F10.rollTopWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F10.rollBottomWork.d_max = Roll.ROLL_F7F10_WORK_MAX;
		F10.rollBottomWork.d_min = Roll.ROLL_F7F10_WORK_MIN;
		F10.rollBottom.d_max = Roll.ROLL_F5F10_OPOR_MAX;
		F10.rollBottom.d_min = Roll.ROLL_F5F10_OPOR_MIN;
		thickness = new Thickness();
		t4 = new Pyrometer(702, 1000);
		t5 = new Pyrometer(702, 1000);
		t6 = new Pyrometer(550, 800);
		wind_1 = new Wind();
		wind_2 = new Wind();
		wind_3 = new Wind();

		parties = new Parties();
		strips = new Strips();
		grades = new Grades();
		posProcent = new PosProcent();
	}

	// ! данные от мастера получены
	public void dataMaster()
	{
		int i;
		/*
		if (klet4a.sis())
		{
			if (klet4a.strip == null)
				klet4a.strip = newStrip();
			klet4a.strip.height4a();
		} else if (klet4a.strip != null)
		{
			klet4a.strip.height4a();
			klet4a.strip = null;
		}
		*/
		if (t1.sis())
		{	// штука под пирометром
			// полосы не было под пирометром
			if (t1.strip == null)
			{
				t1.strip = newSlayb();	// создаем новую
				t1.strip.t1Begin();
			}
			t1.strip.t1();
		} else // под пирометром нет полосы
			if (t1.strip != null)
			{	// штука прошла T1
				t1.strip.t1End();
				t1.strip = null;
			}

		if (t2.sis())
		{	// штука под пирометром
			if (t2.strip == null)
			{
				t2.strip = strips.findStripTo(Stan.locationT2);
				if (t2.strip != null)
					t2.strip.t2Begin();
			}
			if (t2.strip != null)
				t2.strip.t2();
		} else // под пирометром нет полосы
			if (t2.strip != null)
			{
				t2.strip.t2End();
				t2.strip = null;
			}

		if (t3.sis())
		{	// штука под пирометром
			if (t3.strip == null)
			{
				t3.strip = strips.findStripTo(Stan.locationT3);
				if (t3.strip != null)
					t3.strip.t3Begin();
			}
			if (t3.strip != null)
				t3.strip.t3();
		} else if (t3.strip != null)
		{	// под пирометром нет полосы
			t3.strip.t3End();
			t3.strip = null;
		}

		if (F5.sis())
		{	// полоса в клети
			if (F5.strip == null)
			{
				F5.strip = strips.findStripTo(Stan.locationF_5);
				if (F5.strip != null)
					F5.strip.klet5Begin();
			}
			if (F5.strip != null)
				F5.strip.klet5();
		} else // в клети нет полосы
			if (F5.strip != null)
			{
				F5.strip.klet5End();
				F5.strip = null;
			}

		if (F6.sis())
		{	// полоса в клети
			if (F6.strip == null)
			{
				F6.strip = strips.findStripTo(Stan.locationF_6);
				if (F6.strip != null)
					F6.strip.klet6Begin();
			}
			if (F6.strip != null)
				F6.strip.klet6();
		} else // в клети нет полосы
			if (F6.strip != null)
			{
				F6.strip.klet6End();
				F6.strip = null;
			}

		if (F7.sis())
		{	// полоса в клети
			if (F7.strip == null)
			{
				F7.strip = strips.findStripTo(Stan.locationF_7);
				if (F7.strip != null)
					F7.strip.klet7Begin();
			}
			if (F7.strip != null)
				F7.strip.klet7();
		} else // в клети нет полосы

			if (F7.strip != null)
			{
				F7.strip.klet7End();
				F7.strip = null;
			}

		if (F8.sis())
		{	// полоса в клети
			if (F8.strip == null)
			{
				F8.strip = strips.findStripTo(Stan.locationF_8);
				if (F8.strip != null)
					F8.strip.klet8Begin();
			}
			if (F8.strip != null)
				F8.strip.klet8();
		} else // в клети нет полосы
			if (F8.strip != null)
			{
				F8.strip.klet8End();
				F8.strip = null;
			}

		if (F9.sis())
		{	// полоса в клети
			if (F9.strip == null)
			{
				F9.strip = strips.findStripTo(Stan.locationF_9);
				if (F9.strip != null)
					F9.strip.klet9Begin();
			}
			if (F9.strip != null)
				F9.strip.klet9();
		} else // в клети нет полосы

			if (F9.strip != null)
			{
				F9.strip.klet9End();
				F9.strip = null;
			}

		if (F10.sis())
		{	// полоса в клети
			if (F10.strip == null)
			{
				F10.strip = strips.findStripTo(Stan.locationF10);
				if (F10.strip != null)
					F10.strip.klet10Begin();
			}
			if (F10.strip != null)
				F10.strip.klet10();
		} else // в клети нет полосы
			if (F10.strip != null)
			{
				F10.strip.klet10End();
				F10.strip = null;
			}

		if (t4.sis() || t5.sis())
		{	// в толщиномере полоса
			if (thickness.strip == null)
			{
				if (t4.sis())
					thickness.strip = strips.findStripTo(Stan.locationThickness1);
				else
					thickness.strip = strips.findStripTo(Stan.locationThickness2);
				thickness.start();
			}
			if (thickness.strip != null)
				thickness.strip.thickness();
		} else if (thickness.strip != null)
		{
			thickness.strip.thicknessEnd();
			thickness.strip = null;
		}
		// ! моталки
	}


	// 1.новый подкат
	private Strip newSlayb()
	{
		// создание сляба
		Strip strip = new Strip(this);
		// добавить к списку полос
		strips.add(strip);
		// асоциация сляба с партией
		parties.connect(strip);
		return strip;
		// ! добавить в очередь клети 5
	}


	// сохранение состояния
	public void save()
	{
		// сохранить настройки программы
		File f = new File("data\\Stan.xml");
		// определить XMLPage из текущего состояния стана
		XMLPage page = new XMLPage();
		page.addElement(writeToXMLElement("Stan"));
		// сохранить XMLPage
		page.save(f);
		// сохранение данных за смену
		Smena date = new Smena();
		f = new File("data\\" + date.getYear() + "\\" + date.getNameMonth() + "\\" + date.getDay() + "_" + date.getSmen() + ".zip");
		savePartiesStrips(f);
	}

	// загрузка последнего состояния стана
	public void load()
	{
		File f;
		// проверить наличие файла настройки программы
		f = new File("data/Stan.xml");
		if (f.isFile()) // файл настроек существует
		{	// загрузить настройки программы
			Logs.write("Загрузка файла настройки Stan.xml");
			XMLPage page = new XMLPage();
			page.load(f);
			readFromXMLElement(page.findElement("Stan"));
		} else
		{
			Logs.write("Файл настройки программы Stan.xml отсутствует");
			initStan(); // настроуки программы по умолчанию
		}

		Smena date = new Smena();
		// проверить наличие директории за месяц
		f = new File("data\\" + date.getYear() + "\\" + date.getNameMonth());
		if (f.isDirectory())
		{
			f = new File(f, date.getDay() + "_" + date.getSmen() + ".zip");
			// проверить наличие файла за текущюю смену
			if (f.isFile())
			{
				Logs.write("Файл " + f + " уже существует");
				loadPartiesStrips(f); // загрузить данные за текущюю смену
				Logs.write("Файл " + f + " загружен");
			}
		} else
		{
			Logs.write("Папка за текущий месяц отсутствует");
			f.mkdirs();
			Logs.write("Папка за текущий месяц создана");
		}
	}

	// загрузка настроек программы
	public void readFromXMLElement(XMLElement element)
	{
		if (element != null)
		{
			F5.readFromXMLElement(element.findElement("F5"));
			F6.readFromXMLElement(element.findElement("F6"));
			F7.readFromXMLElement(element.findElement("F7"));
			F8.readFromXMLElement(element.findElement("F8"));
			F9.readFromXMLElement(element.findElement("F9"));
			F10.readFromXMLElement(element.findElement("F10"));
			grades.readFromXMLElement(element.findElement("Grades"));
			posProcent.readFromXMLElement(element.findElement("Pos"));
		} else
		{
			Logs.write("Stan.xml повреждён");
			initStan();
		}
	}

	// !!!
	public XMLElement writeToXMLElement(String name)
	{
		XMLElement element = new XMLElement(name);
		element.addElement(F5.writeToXMLElement("F5"));
		element.addElement(F6.writeToXMLElement("F6"));
		element.addElement(F7.writeToXMLElement("F7"));
		element.addElement(F8.writeToXMLElement("F8"));
		element.addElement(F9.writeToXMLElement("F9"));
		element.addElement(F10.writeToXMLElement("F10"));
		element.addElement(grades.writeToXMLElement("Grades"));
		element.addElement(posProcent.writeToXMLElement("Pos"));
		XMLElement temp = new XMLElement("Time");
		temp.addAttribute(new XMLAttribute("close", new Smena().toStringDateTime()));
		return element;
	}

	// !! настройка программы по умолчанию
	private void initStan()
	{
		grades.init();
		posProcent.init();
	}

	// !!!
	private void loadPartiesStrips(File file)
	{
		try
		{
			InputStream is = new FileInputStream(file);
			ZipInputStream in = new ZipInputStream(is);
			// загрузка сведений о партиях
			if (in.getNextEntry().getName().equals("parties"))
				parties.load(in);
			// загрузка сведений о полосах
			if (in.getNextEntry().getName().equals("strips"))
				strips.load(in);
			in.close();
			is.close();
		}
		catch (IOException e)
		{

		}


	}

	// !!! 20080519
	private void savePartiesStrips(File file)
	{
		try
		{
			OutputStream os = new FileOutputStream(file);
			ZipOutputStream out = new ZipOutputStream(os);
			// сохранить сведений о партиях
			out.putNextEntry(new ZipEntry("parties"));
			parties.save(out);
			// сохранить сведений о полосах
			out.putNextEntry(new ZipEntry("strips"));
			strips.save(out);
			out.close();
			os.close();
		}
		catch (IOException e)
		{

		}
	}
}