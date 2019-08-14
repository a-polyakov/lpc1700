package lpc1700.okna;

import lpc1700.Index;
import lpc1700.Logs;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.10.2007
 * Time: 14:55:31
 * Панель растределения нагрузок
 */
public class PanelPos extends JPanel implements ActionListener, ChangeListener
{
	private JComboBox pos_procentChoice;
	private JSlider pos_procentN5, pos_procentN6, pos_procentN7,
			pos_procentN8, pos_procentN9, pos_procentN10;
	private JLabel pos_procentN5Label, pos_procentN6Label, pos_procentN7Label,
			pos_procentN8Label, pos_procentN9Label, pos_procentN10Label;
	private JLabel pos_procent_sum;
	private JButton pos_procentSave;

	private Index index;

	public PanelPos(Index index)
	{
		super(new BorderLayout(0, 0));
		this.index = index;
		JPanel center = new JPanel(new GridLayout(0, 12));
		JPanel down = new JPanel();
		pos_procentChoice = new JComboBox();
		byte temp1[][] = index.stan.posProcent.getListPosProcent();
		for (int i = 0; i < temp1.length; i++)
			pos_procentChoice.addItem(stringPosProcent(temp1[i]));
		byte temp[] = index.stan.posProcent.getPosProcent();
		pos_procentChoice.setSelectedIndex(-1);
		pos_procentChoice.addActionListener(this);
		add(pos_procentChoice, BorderLayout.NORTH);
		pos_procentN5 = new JSlider(JSlider.VERTICAL, 0, 33, temp[0]);
		pos_procentN5.setMajorTickSpacing(5);
		pos_procentN5.setMinorTickSpacing(1);
		pos_procentN5.setPaintTicks(true);
		pos_procentN5.setPaintLabels(true);
		pos_procentN5.addChangeListener(this);
		pos_procentN5.setSize(30, 300);
		center.add(pos_procentN5);
		pos_procentN5Label = new JLabel(temp[0] + "%");
		center.add(pos_procentN5Label);
		pos_procentN6 = new JSlider(JSlider.VERTICAL, 0, 33, temp[1]);
		pos_procentN6.setMajorTickSpacing(5);
		pos_procentN6.setMinorTickSpacing(1);
		pos_procentN6.setPaintTicks(true);
		pos_procentN6.setPaintLabels(true);
		pos_procentN6.addChangeListener(this);
		center.add(pos_procentN6);
		pos_procentN6Label = new JLabel(temp[1] + "%");
		center.add(pos_procentN6Label);
		pos_procentN7 = new JSlider(JSlider.VERTICAL, 0, 33, temp[2]);
		pos_procentN7.setMajorTickSpacing(5);
		pos_procentN7.setMinorTickSpacing(1);
		pos_procentN7.setPaintTicks(true);
		pos_procentN7.setPaintLabels(true);
		pos_procentN7.addChangeListener(this);
		center.add(pos_procentN7);
		pos_procentN7Label = new JLabel(temp[2] + "%");
		center.add(pos_procentN7Label);
		pos_procentN8 = new JSlider(JSlider.VERTICAL, 0, 33, temp[3]);
		pos_procentN8.setMajorTickSpacing(5);
		pos_procentN8.setMinorTickSpacing(1);
		pos_procentN8.setPaintTicks(true);
		pos_procentN8.setPaintLabels(true);
		pos_procentN8.addChangeListener(this);
		center.add(pos_procentN8);
		pos_procentN8Label = new JLabel(temp[3] + "%");
		center.add(pos_procentN8Label);
		pos_procentN9 = new JSlider(JSlider.VERTICAL, 0, 33, temp[4]);
		pos_procentN9.setMajorTickSpacing(5);
		pos_procentN9.setMinorTickSpacing(1);
		pos_procentN9.setPaintTicks(true);
		pos_procentN9.setPaintLabels(true);
		pos_procentN9.addChangeListener(this);
		center.add(pos_procentN9);
		pos_procentN9Label = new JLabel(temp[4] + "%");
		center.add(pos_procentN9Label);
		pos_procentN10 = new JSlider(JSlider.VERTICAL, 0, 33, temp[5]);
		pos_procentN10.setMajorTickSpacing(5);
		pos_procentN10.setMinorTickSpacing(1);
		pos_procentN10.setPaintTicks(true);
		pos_procentN10.setPaintLabels(true);
		pos_procentN10.addChangeListener(this);
		center.add(pos_procentN10);
		pos_procentN10Label = new JLabel(temp[5] + "%");
		center.add(pos_procentN10Label);
		add(center, BorderLayout.CENTER);
		pos_procent_sum = new JLabel("всего " +
				(temp[0] + temp[1] +
						temp[2] + temp[3] +
						temp[4] + temp[5]) + "%");
		down.add(pos_procent_sum);
		pos_procentSave = new JButton("Применить");
		pos_procentSave.addActionListener(this);
		down.add(pos_procentSave);
		add(down, BorderLayout.SOUTH);
	}

	// событие изменение положения ползунков
	public void stateChanged(ChangeEvent e)
	{
		Object event = e.getSource();
		if (pos_procentN5 == event || pos_procentN6 == event || pos_procentN7 == event ||
				pos_procentN8 == event || pos_procentN9 == event || pos_procentN10 == event)
		{
			if (pos_procentN5 == event)
			{
				pos_procentN5Label.setText(pos_procentN5.getValue() + "%");
			} else if (pos_procentN6 == event)
			{
				pos_procentN6Label.setText(pos_procentN6.getValue() + "%");
			} else if (pos_procentN7 == event)
			{
				pos_procentN7Label.setText(pos_procentN7.getValue() + "%");
			} else if (pos_procentN8 == event)
			{
				pos_procentN8Label.setText(pos_procentN8.getValue() + "%");
			} else if (pos_procentN9 == event)
			{
				pos_procentN9Label.setText(pos_procentN9.getValue() + "%");
			} else // if (pos_procentN10 == event)
			{
				pos_procentN10Label.setText(pos_procentN10.getValue() + "%");
			}
			int sum = pos_procentN5.getValue() +
					pos_procentN6.getValue() +
					pos_procentN7.getValue() +
					pos_procentN8.getValue() +
					pos_procentN9.getValue() +
					pos_procentN10.getValue();
			pos_procent_sum.setText("всего " + sum + "%");
			if (sum == 100)
				pos_procentSave.setEnabled(true);
			else if (pos_procentSave.isEnabled())
				pos_procentSave.setEnabled(false);
		}
	}

	// событие нажапия на кнопку, выбор элемента из списка
	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (pos_procentChoice == event)
		{	// выбор процентов распределения обжатия из списка
			int i = pos_procentChoice.getSelectedIndex();
			index.stan.posProcent.setPosProcent(i);
			byte temp[] = index.stan.posProcent.getPosProcent();
			pos_procentN5.setValue(temp[0]);
			pos_procentN6.setValue(temp[1]);
			pos_procentN7.setValue(temp[2]);
			pos_procentN8.setValue(temp[3]);
			pos_procentN9.setValue(temp[4]);
			pos_procentN10.setValue(temp[5]);
			pos_procentChoice.setSelectedIndex(-1);
			Logs.write("Выбран процент обжатия: " + stringPosProcent(temp));
		} else if (pos_procentSave == event)
		{	// сохранить проценты распределения обжатия
			byte temp[] = new byte[]{(byte) pos_procentN5.getValue(),
					(byte) pos_procentN6.getValue(),
					(byte) pos_procentN7.getValue(),
					(byte) pos_procentN8.getValue(),
					(byte) pos_procentN9.getValue(),
					(byte) pos_procentN10.getValue()};
			if (index.stan.posProcent.setPosProcent(temp))
				pos_procentChoice.addItem(stringPosProcent(temp));
			Logs.write("Выбран процент обжатия: " + stringPosProcent(temp));
		}
	}

	// строка для списка
	public String stringPosProcent(byte[] arrayByte)
	{
		return arrayByte[0] + " - " + arrayByte[1] + " - " +
				arrayByte[2] + " - " + arrayByte[3] + " - " +
				arrayByte[4] + " - " + arrayByte[5];
	}
}