package lpc1700.okna;

import lpc1700.Smena;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 12.12.2007
 * Time: 8:43:26
 * панель истории программы
 */
public class PanelLog extends JPanel implements ActionListener
{
	private JTextArea log;
	private JButton logSave;
	private JCheckBox logAutoSave;

	public PanelLog()
	{
		super(new BorderLayout());
		log = new JTextArea();
		log.setEditable(false);
		add(new JScrollPane(log), BorderLayout.CENTER);
		JPanel p1 = new JPanel();
		logSave = new JButton("Сохранить");
		logSave.addActionListener(this);
		p1.add(logSave);
		logAutoSave = new JCheckBox("Сохранять раз в день");
		p1.add(logAutoSave);
		add(p1, BorderLayout.SOUTH);
	}

	public void write(String s)
	{
		log.insert(new Smena().toStringDateTime() + "\t" + s + "\n", 0);
	}
	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (logSave == event)
		{	// сохранить историю программы

		}
	}
}
