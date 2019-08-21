package lpc1700.okna;

import lpc1700.stan.KletChistovaya;
import lpc1700.util.field.JFloatField;
import lpc1700.util.field.JIntegerField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 28.11.2007
 * Time: 11:51:19
 * Окно ввода информации о валках
 * <p/>
 * черновая группа клетей
 */
public class DialogRolls extends JDialog implements ActionListener
{
	private JLabel kletLabel;
	private JIntegerField rollTop_nb;
	private JFloatField rollTop_diametr;
	private JFloatField rollTop_crown;
	private JIntegerField rollTopWork_nb;
	private JFloatField rollTopWork_diametr;
	private JFloatField rollTopWork_crown;
	private JIntegerField rollBottomWork_nb;
	private JFloatField rollBottomWork_diametr;
	private JFloatField rollBottomWork_crown;
	private JIntegerField rollBottom_nb;
	private JFloatField rollBottom_diametr;
	private JFloatField rollBottom_crown;

	private JButton exit;
	private JButton save;

	private KletChistovaya klet;

	public DialogRolls()
	{
		setTitle("Информация о волках");
		Dimension d = new Dimension(500, 200);
		setMinimumSize(d);
		setSize(d);
		setModal(true);
		setLayout(new BorderLayout());
		JPanel center = new JPanel(new GridLayout(5, 4));
		kletLabel = new JLabel("Клеть");
		center.add(kletLabel);
		center.add(new JLabel("Номер"));
		center.add(new JLabel("Диаметр"));
		center.add(new JLabel("Бочкообразность"));
		center.add(new JLabel("Верхний"));
		rollTop_nb = new JIntegerField();
		center.add(rollTop_nb);
		rollTop_diametr = new JFloatField(1.3f);
		center.add(rollTop_diametr);
		rollTop_crown = new JFloatField();
		center.add(rollTop_crown);
		center.add(new JLabel("Верхний рабочий"));
		rollTopWork_nb = new JIntegerField();
		center.add(rollTopWork_nb);
		rollTopWork_diametr = new JFloatField(0.67f);
		center.add(rollTopWork_diametr);
		rollTopWork_crown = new JFloatField();
		center.add(rollTopWork_crown);
		center.add(new JLabel("Нижний рабочий"));
		rollBottomWork_nb = new JIntegerField();
		center.add(rollBottomWork_nb);
		rollBottomWork_diametr = new JFloatField(0.67f);
		center.add(rollBottomWork_diametr);
		rollBottomWork_crown = new JFloatField();
		center.add(rollBottomWork_crown);
		center.add(new JLabel("Нижний"));
		rollBottom_nb = new JIntegerField();
		center.add(rollBottom_nb);
		rollBottom_diametr = new JFloatField(1.3f);
		center.add(rollBottom_diametr);
		rollBottom_crown = new JFloatField();
		center.add(rollBottom_crown);
		add(center, BorderLayout.CENTER);
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		exit = new JButton("Отмена");
		exit.addActionListener(this);
		bottom.add(exit);
		save = new JButton("Сохранить");
		save.addActionListener(this);
		bottom.add(save);
		add(bottom, BorderLayout.SOUTH);
	}

	public void setKlet(KletChistovaya klet)
	{
		this.klet = klet;
		kletLabel.setText("Клеть " + klet.name);
		rollTop_nb.setValue(klet.rollTop.nb);
		rollTop_diametr.setValue(klet.rollTop.diametr);
		rollTop_crown.setValue(klet.rollTop.crown);
		rollTopWork_nb.setValue(klet.rollTopWork.nb);
		rollTopWork_diametr.setValue(klet.rollTopWork.diametr);
		rollTopWork_crown.setValue(klet.rollTopWork.crown);
		rollBottomWork_nb.setValue(klet.rollBottomWork.nb);
		rollBottomWork_diametr.setValue(klet.rollBottomWork.diametr);
		rollBottomWork_crown.setValue(klet.rollBottomWork.crown);
		rollBottom_nb.setValue(klet.rollBottom.nb);
		rollBottom_diametr.setValue(klet.rollBottom.diametr);
		rollBottom_crown.setValue(klet.rollBottom.crown);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (save == event)
		{
			klet.rollTop.nb = rollTop_nb.getValue();
			klet.rollTop.diametr = rollTop_diametr.getValue();
			klet.rollTop.crown = rollTop_crown.getValue();
			klet.rollTopWork.nb = rollTopWork_nb.getValue();
			klet.rollTopWork.diametr = rollTopWork_diametr.getValue();
			klet.rollTopWork.crown = rollTopWork_crown.getValue();
			klet.rollBottomWork.nb = rollBottomWork_nb.getValue();
			klet.rollBottomWork.diametr = rollBottomWork_diametr.getValue();
			klet.rollBottomWork.crown = rollBottomWork_crown.getValue();
			klet.rollBottom.nb = rollBottom_nb.getValue();
			klet.rollBottom.diametr = rollBottom_diametr.getValue();
			klet.rollBottom.crown = rollBottom_crown.getValue();
			setVisible(false);
		} else if (exit == event)
		{
			setVisible(false);
		}
	}

	public static void main(String arg[])
	{
		DialogRolls d = new DialogRolls();
		d.setVisible(true);
	}
}
