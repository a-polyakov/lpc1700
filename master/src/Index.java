import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.09.2007
 * Time: 10:00:43
 * Соединятся с PC берет данные разбирает их на данные от мастера и данные PDI отсылает их ЛПЦ
 */
public class Index extends Frame implements WindowListener, ActionListener
{
	public static int LENGTH_MASTER = 1326;	// 663 слова
	public static int LENGTH_PDI = 52;
	public static String LPC_SOCET_IP = "127.0.0.1";
	public static int MASTER_LPC_SOCET_PORT = 12345;
	public static int PDI_LPC_SOCET_PORT = 12346;
	public static final int NEW_PARTIYA=10;
	public static String FILE_ERROR = "error.log";

	private JCheckBox netPcRunCheck;			// флаг управляющий соединением с PC
	private JCheckBox netPcConnectCheck;		// флаг сигнализирующий о наличии соединения с PC
	private JCheckBox netMasterLpcRunCheck;		// флаг сигнализирующий о наличии соединения с ЛПЦ
	private JCheckBox netMasterLpcConnectCheck;
	private JCheckBox netPdiLpcRunCheck;
	private JCheckBox netPdiLpcConnectCheck;
	private JButton sendButton;
	public JCheckBox sendAuto;

	public JCheckBox fileSave;
	public JCheckBox fileLoad;
	public JButton fileChoice;
	private JLabel fileName;

	public NetLpc netMasterLpc;	// соединение мастера с ЛПЦ
	public NetLpcPdi netPdiLpc;
	public NetPC netPc;				// соединение с РС

	private JTextArea log;
	private JButton logSave;
	private JCheckBox logAutoSave;


	public Index()
	{
		super("master");
		Dimension d = new Dimension(640, 480);
		setSize(d);
		setMinimumSize(d);
		JTabbedPane panel = new JTabbedPane(JTabbedPane.TOP);
		panel.add(panNet(), "Сеть");
		panel.add(panLog(), "История");
		add(panel);
		addWindowListener(this);
		setVisible(true);
	}


	private JPanel panNet()
	{
		JPanel p = new JPanel(new GridLayout(4, 2));

		netPcRunCheck = new JCheckBox("соединение с PC");
		netPcRunCheck.addActionListener(this);
		p.add(netPcRunCheck);
		netPcConnectCheck = new JCheckBox("работает");
		netPcConnectCheck.setEnabled(false);
		p.add(netPcConnectCheck);

		netMasterLpcRunCheck = new JCheckBox("Мастер -> ЛПЦ");
		netMasterLpcRunCheck.addActionListener(this);
		p.add(netMasterLpcRunCheck);
		netMasterLpcConnectCheck = new JCheckBox("работает");
		netMasterLpcConnectCheck.setEnabled(false);
		p.add(netMasterLpcConnectCheck);

		netPdiLpcRunCheck = new JCheckBox("PDI -> ЛПЦ");
		netPdiLpcRunCheck.addActionListener(this);
		p.add(netPdiLpcRunCheck);
		netPdiLpcConnectCheck = new JCheckBox("работает");
		netPdiLpcConnectCheck.setEnabled(false);
		p.add(netPdiLpcConnectCheck);

		sendButton = new JButton("Send");
		sendButton.setEnabled(false);
		sendButton.addActionListener(this);
		p.add(sendButton);
		sendAuto = new JCheckBox("Авто");
		p.add(sendAuto);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(p);
		fileSave=new JCheckBox("Сохранять в файл");
		p1.add(fileSave);
		fileLoad=new JCheckBox("Данные из файла");
		p1.add(fileLoad);
		fileChoice=new JButton("Файл");
		fileChoice.addActionListener(this);
		p1.add(fileChoice);
		fileName=new JLabel("имя");
		p1.add(fileName);
		return p1;
	}

	private JPanel panLog()
	{
		JPanel p = new JPanel(new BorderLayout());

		log = new JTextArea();
		log.setEditable(false);
		p.add(new JScrollPane(log),BorderLayout.CENTER);
		JPanel downPanel = new JPanel();
		logSave = new JButton("Сохранить");
		downPanel.add(logSave, BorderLayout.WEST);
		logAutoSave = new JCheckBox("Сохранять раз в день");
		downPanel.add(logAutoSave, BorderLayout.CENTER);
		p.add(downPanel,BorderLayout.SOUTH);
		return p;
	}

	public void writeLog(String s)
	{
		log.insert(new Date().toLocaleString() + "\t" + s + "\n", 0);
	}

	public static void main(String arg[])
	{
		Index index = new Index();
	}

	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (sendButton == event)
		{
			netPc.send();
		} else if (netPcRunCheck == event)
		{
			if (netPcRunCheck.isSelected())
			{	// Установить соединение
				netPc = new NetPC(this);
				netPc.start();
				netPcConnectCheck.setSelected(true);
			} else
			{	//Разорвать соединение
				netPc.close();
				netPcConnectCheck.setSelected(false);
			}
		} else if (netMasterLpcRunCheck == event)
		{
			if (netMasterLpcRunCheck.isSelected())
			{	// Установить соединение
				netMasterLpc = new NetLpc(this, LPC_SOCET_IP, MASTER_LPC_SOCET_PORT);
				netMasterLpc.start();
			} else
			{	//Разорвать соединение
				netMasterLpc.close();
			}
		} else if (netPdiLpcRunCheck == event)
		{
			if (netPdiLpcRunCheck.isSelected())
			{	// Установить соединение
				netPdiLpc = new NetLpcPdi(this, LPC_SOCET_IP, PDI_LPC_SOCET_PORT);
				netPdiLpc.start();
			} else
			{	//Разорвать соединение
				netPdiLpc.close();
			}
		}
		else if (fileChoice==event)
		{
			FileDialog d=new FileDialog(this);
			d.setVisible(true);
			if (d.getFile()!=null)
			{
				fileName.setText(d.getDirectory()+d.getFile());
				netPc.setInFile(fileName.getText());
			}

		}
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		dispose();
		System.exit(0);
	}

	public void windowClosed(WindowEvent e)
	{
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

	// обработка сообщений сети
	public void netEventConnect(Object e)
	{
		if (netMasterLpc == e)
		{
			netMasterLpcConnectCheck.setSelected(true);
			sendButton.setEnabled(true);
			writeLog("соединение Мастера -> ЛПЦ установлено");
		} else if (netPdiLpc == e)
		{
			netPdiLpcConnectCheck.setSelected(true);
			writeLog("соединение PDI -> ЛПЦ установлено");
		}
	}

	public void netEventClose(Object e)
	{
		if (netMasterLpc == e)
		{
			netMasterLpcConnectCheck.setSelected(false);
			sendButton.setEnabled(false);
			writeLog("соединение Мастера -> ЛПЦ разорвано");
		} else if (netPdiLpc == e)
		{
			netPdiLpcConnectCheck.setSelected(false);
			writeLog("соединение PDI -> ЛПЦ разорвано");
		}
	}

	public void netEventError(Object e, String mes)
	{
		if (netMasterLpc == e)
		{
			netMasterLpcConnectCheck.setSelected(false);
			sendButton.setEnabled(false);
			writeLog("ошибка соединения Мастера с ЛПЦ: " + mes);
		} else if (netPdiLpc == e)
		{
			netPdiLpcConnectCheck.setSelected(false);
			writeLog("ошибка соединения PDI с ЛПЦ: " + mes);
		}

	}
}
