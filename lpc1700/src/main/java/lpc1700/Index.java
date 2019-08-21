package lpc1700;

import lpc1700.network.NetMaster;
import lpc1700.network.NetPdi;
import lpc1700.okna.*;
import lpc1700.stan.Party;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.08.2007
 * Time: 8:35:12
 * Окно приложения
 * <p/>
 * слежение за штукой
 * <p/>
 * страница настроек
 * страница сдвижки
 * загрузка/сохранение настроек программы
 * - редуктор клети
 * - диамерт валков время установки
 * скорость прокатки
 * - а постояная скорость, v~10-12 m/c
 * - б до захвата моталкой v1 и плавное ускорение до v2
 * - в до захвата моталкой v1 резкое ускорение до v2, удерживание скорости на на одном уровне
 * - г разгон при выходе из 10 клепи замедление для заправки полосы моталкой резкий разгон до v2
 * - д разгон при выходе из 10 клепи замедление для заправки полосы моталкой плавное ускорение до v2
 * <p/>
 * порядок выполнения расчетов
 * 1 ввыдены данные о партии
 * > T, Hin, Hout, speed(для стана), марка стали
 * ожидаем сведения о появлении штуки этой партии
 * 2 раскат в клети 4а
 * > Hi расчетные значения толщин полученые с 4а
 * 3 полоса под пирометром Т1
 * > Ti значение температуры полученые с Т1 на i участке полосы
 * - +расщет толщин после каждой клети
 * - определение скорости полосы между клетями
 * - определение скорости клети
 * - расчитать по модели растворы всех клетей
 * - добавить в очередь новые настройки (скорость, позиции для участков, )
 * - расчитать по модели растворы всех клетей
 * - выставить растворы у всех клетей
 * 4 полоса проходит ножницы
 * > Скорость рольганга ножниц, сигнал обрезки
 * - выбросить участки обрезаные ножницами
 * 5 полоса под пирометром Т2 - ножницы
 * > Ti значение температуры полученые с Т2 на i участке полосы
 * - расчитать по модели растворы всех клетей
 * - выставить растворы у всех клетей
 * - адаптация модели на участке Т1 - Т2
 * 6 полоса под пирометром гидрозбива Т3
 * > Ti значение температуры полученые с Т3 на i участке полосы
 * - расчитать по модели растворы всех клетей
 * - выставить растворы у всех клетей
 * - адаптация модели на участке Т2 - Т3
 * 7 полоса в клети 5
 * > Hi расчетные значения толщин полученые с 5 клети
 * - корекция Ti полосы
 * - адаптация модели на участке Т3 - F5
 * ...
 * 12 полоса в клети 10
 * > Hi расчетные значения толщин полученые с 10 клети
 * 13 полоса в толщиномере
 * > фактическая толщина
 * - корекция модели для марки стали
 * <p/>
 * прорисовка панели партии не должна постоянно выполнятся
 * сохранение истории прокатки каждую смену(или при закрытии программы),
 * при старте программы пытаться загрузити историю за текущюю смену
 * - при загрузке виснет на разархивировании
- процент обжатия отку да берётся
- график толщины для длинных полос
- масштабируемость картинки стана
- показывать количество прокатаных за смену
- кнопки клетей наследываются от Canvas
- сортамент переход на новую запись при ее создании (окно редактирования),
  проверка повторений, сортировка по столбцам
- система слижения проверка на ложное сробатывание датчиков
- математическая модель
- марка стали подставляется в партию и наоборот
- подвисает процесс соединения с PC
- перевалка сохраняется в журнале
- сохранять данные за смену
- перемещение сляба между партиями
- сбой при сохранении данных при закрытии программы
- растворы черновой группы клетей
- экраны на промежуточном рольганге
- какие данные передаются мастеру
- уточнить формулу нахождения SIS и SOS клети

 - 01-4 клеть отсутствует сигналы не работает, открыты
 - пирометр 6 отсутствует сигнал не ратотает
 - сигнал петледержателей охлождение отключено не меняется
 */
public class Index extends JFrame
		implements WindowListener, ActionListener
{
	public static String FILE_ERROR = "error.log";
	public static int SOCKET_PORT_MASTER = 12345;
	public static int SOCKET_PORT_PDI = 12346;
	private long time;
	private int closeStep;
	private NetMaster netMaster;			// соединение с мастером
	private NetPdi netPdi;					// соединение с приложение ввода данных о партиях
	public Stan stan;

	private DrawStan panStan;				// панель состояния стана
	private PanelPos panPos;				// панель проценты обжатия
	private PanelParty panParty;			// панель данных о партиях и штуках
	private PanelPerevalka panPerevalka;	// панель данные о валках
	private PanelGrade panGrade;			// панель спрабочник о металах
	private PanelZeroing panZeroing;		// панель данных обнуления
	private PanelLog panLog;				// панель истории программы

	// Сеть
	private JCheckBox masterRun;
	private JCheckBox masterConnect;
	public JLabel masterTime;
	private JCheckBox pdiRun;
	private JCheckBox pdiConnect;
	public JLabel pdiTime;
	private JTextArea logNetwork;
	private JButton logNetworkSave;
	private JCheckBox logNetworkAutoSave;
	
	public Index()
	{
		super("ЛПЦ 1700");
		Dimension d = new Dimension(800, 600);
		setSize(d);
		setMinimumSize(d);
		JTabbedPane panel = new JTabbedPane(JTabbedPane.TOP);
		panLog=new PanelLog();
		Logs.panLog = panLog;
		stan = new Stan();
		stan.load();
		panStan = new DrawStan(stan);
		panel.add(panStan, "Стан");
		panPos = new PanelPos(this);
		panel.add(panPos, "Растворы клетей");
		panParty = new PanelParty(stan);
		panel.add(panParty, "Партии");
		panPerevalka = new PanelPerevalka(stan);
		panel.add(panPerevalka, "Перевалка");
		panGrade = new PanelGrade(this);
		panel.add(panGrade, "Сортамент");
		panZeroing = new PanelZeroing();
		panel.add(panZeroing, "Обнуление");
		panel.add(panNet(), "Сеть");
		panel.add(panLog, "История");
		add(panel);
		setVisible(true);
		addWindowListener(this);
		netMaster = new NetMaster(this);
		netMaster.start();
		netPdi = new NetPdi(this);
		netPdi.start();
		Logs.write("Программа запущена");
		closeStep=0;
	}

	// панель сети
	private JPanel panNet()
	{
		JPanel p = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
		JPanel masPanel = new JPanel();
		masterRun = new JCheckBox("Мастер");
		masterRun.addActionListener(this);
		masPanel.add(masterRun);
		masterConnect = new JCheckBox("соединение", false);
		masterConnect.setEnabled(false);
		masPanel.add(masterConnect);
		masterTime = new JLabel("XX:XX:XX:XXX");
		masPanel.add(masterTime);
		topPanel.add(masPanel);
		JPanel pdiPanel = new JPanel();
		pdiRun = new JCheckBox("PDI");
		pdiRun.addActionListener(this);
		pdiPanel.add(pdiRun);
		pdiConnect = new JCheckBox("соединение", false);
		pdiConnect.setEnabled(false);
		pdiPanel.add(pdiConnect);
		pdiTime = new JLabel("XX:XX:XX:XXX");
		pdiPanel.add(pdiTime);
		topPanel.add(pdiPanel);
		p.add(topPanel, BorderLayout.NORTH);
		logNetwork = new JTextArea();
		logNetwork.setEditable(false);
		p.add(new JScrollPane(logNetwork), BorderLayout.CENTER);
		JPanel downPanel = new JPanel();
		logNetworkSave = new JButton("Сохранить");
		downPanel.add(logNetworkSave);
		logNetworkAutoSave = new JCheckBox("Сохранять раз в день");
		downPanel.add(logNetworkAutoSave);
		p.add(downPanel, BorderLayout.SOUTH);
		return p;
	}

	// получены новые данные от мастера
	public void newData()
	{
		// перерисовать стан
		panStan.repaint();
		panParty.repaint();
		masterTime.setText(stan.timeMaster.toStringTime());
		// данные стана обновились
		stan.dataMaster();
	}

	public void writeLogNetwork(String s)
	{
		logNetwork.insert(new Smena().toStringDateTime() + "\t" + s + "\n", 0);

	}

	// новая партия
	public void newPartiya(Party party)
	{
		stan.parties.add(party);
		pdiTime.setText(new Smena().toStringTime());
	}

	public static void main(String arg[])
	{
		Index index = new Index();
	}

	// сообытия окна
	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{	// !!!
		//if (System.currentTimeMillis()-time<2000)
		//{
		//	if (closeStep>1)
		//	{
				netMaster.close();
				stan.save();
				dispose();
				System.exit(0);
		/*	}
			else
			{
				closeStep++;
				time=System.currentTimeMillis();
			}
		}
		else
		{
			closeStep=0;
			time=System.currentTimeMillis();
		}
		*/
	}

	public void windowClosed(WindowEvent e)
	{
		time=System.currentTimeMillis();
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	// событие нажапия на кнопку, выбор элемента из списка
	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (masterRun == event)
		{
			if (masterRun.isSelected())
			{
				netMaster = new NetMaster(this);
				netMaster.start();
			} else
			{
				netMaster.close();
			}
		} else if (pdiRun == event)
		{
			if (pdiRun.isSelected())
			{
				netPdi = new NetPdi(this);
				netPdi.start();
			} else
			{
				netPdi.close();
			}
		}
	}

	// события сети
	public void netEventRun(Object event)
	{
		if (netMaster == event)
		{
			masterRun.setSelected(true);
			writeLogNetwork("Сервер MASTER запущен");
		} else if (netPdi == event)
		{
			pdiRun.setSelected(true);
			writeLogNetwork("Сервер PDI запущен");
		}
	}

	public void netEventStop(Object event)
	{
		if (netMaster == event)
		{
			masterRun.setSelected(false);
			writeLogNetwork("Сервер MASTER остановлен");
		} else if (netPdi == event)
		{
			pdiRun.setSelected(false);
			writeLogNetwork("Сервер PDI остановлен");
		}
	}

	public void netEventConnect(Object event)
	{
		if (netMaster == event)
		{
			masterConnect.setSelected(true);
			writeLogNetwork("Соединение MASTER установлено");
		} else if (netPdi == event)
		{
			pdiConnect.setSelected(true);
			writeLogNetwork("Соединение PDI установлено");
		}
	}

	public void netEventClose(Object event)
	{
		if (netMaster == event)
		{
			masterConnect.setSelected(false);
			writeLogNetwork("Соединение MASTER разорвано");
		} else if (netPdi == event)
		{
			pdiConnect.setSelected(false);
			writeLogNetwork("Соединение PDI разорвано");
		}
	}

	public void netEventError(Object event, String mes)
	{
		if (netMaster == event)
		{
			masterConnect.setSelected(false);
			writeLogNetwork("Ошибка сети MASTER: " + mes);
		} else if (netPdi == event)
		{
			pdiConnect.setSelected(false);
			writeLogNetwork("Ошибка сети PDI: " + mes);
		}
	}
}
