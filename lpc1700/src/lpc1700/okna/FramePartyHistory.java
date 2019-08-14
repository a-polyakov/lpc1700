package lpc1700.okna;

import lpc1700.Smena;
import lpc1700.stan.Parties;
import lpc1700.stan.Strips;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 29.10.2007
 * Time: 14:25:39
 * Окно просмотра истории прокатки партий штуки по сменам
 * <p/>
 * загрузка истории
 */
public class FramePartyHistory extends JFrame implements ActionListener
{
	private JButton previous;
	private JComboBox year;
	private JComboBox month;
	private JComboBox day;
	private JComboBox smen;
	private JButton next;
	private JTable tablePartiy;
	private JTable tableStrip;

	private Parties parties;
	private Strips strips;

	private Smena date;


	public FramePartyHistory()
	{
		super("История прокатки");
		Dimension d = new Dimension(640, 480);
		setSize(d);
		setMinimumSize(d);
		setLayout(new BorderLayout());
		date = new Smena();
		JPanel panTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
		previous = new JButton("Предыдущий");
		previous.addActionListener(this);
		panTop.add(previous);
		year = new JComboBox(new String[]{"2007"}); // !! по именам дерикторий
		year.addActionListener(this);
		panTop.add(year);
		month = new JComboBox(Smena.nameMonth);
		month.addActionListener(this);
		panTop.add(month);
		day = new JComboBox(getDays());
		day.addActionListener(this);
		panTop.add(day);
		smen = new JComboBox(new String[]{"1", "2", "3"});
		smen.addActionListener(this);
		panTop.add(smen);
		next = new JButton("Следующий");
		next.addActionListener(this);
		panTop.add(next);
		changDate();
		add(panTop, BorderLayout.NORTH);

		JSplitPane panCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		panCenter.setDividerSize(8);
		panCenter.setOneTouchExpandable(true);
		panCenter.setDividerLocation(300);

		parties = new Parties();
		strips = new Strips();

		tablePartiy = new JTable(parties);
		tablePartiy.setColumnSelectionAllowed(true);
		tablePartiy.setRowSelectionAllowed(true);
		panCenter.setTopComponent(new JScrollPane(tablePartiy));

		tableStrip = new JTable(strips);
		tableStrip.setColumnSelectionAllowed(true);
		tableStrip.setRowSelectionAllowed(true);
		panCenter.setBottomComponent(new JScrollPane(tableStrip));

		add(panCenter, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (previous == event)
		{	// !!
			date.decSmen();
			changDate();
		} else if (next == event)
		{	// Следующий
			date.incSmen();
			changDate();
		} else if (smen == event)
		{
			date.setSmen((byte) (smen.getSelectedIndex() + 1));
			changDate();
		} else if (day == event)
		{
			date.setDay((byte) (day.getSelectedIndex() + 1));
			changDate();
		} else if (month == event)
		{
			date.setMonth((byte) (month.getSelectedIndex() + 1));
			changDate();
		} else if (year == event)
		{
			date.setYear((short) (year.getSelectedIndex() + 1));
			changDate();
		}
	}

	// дата изменина
	private void changDate()
	{
		year.setSelectedItem(String.valueOf(date.getYear()));
		month.setSelectedIndex(date.getMonth() - 1);
		day.setSelectedIndex(date.getDay() - 1);
		smen.setSelectedIndex(date.getSmen() - 1);
		// !! обновить таблицы
	}

	private String[] getDays()
	{
		String s[] = new String[31];
		for (int i = 0; i < 31; i++)
			s[i] = String.valueOf(i + 1);
		return s;
	}
}
