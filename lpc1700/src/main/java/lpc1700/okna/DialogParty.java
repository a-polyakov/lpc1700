package lpc1700.okna;

import lpc1700.stan.Party;
import lpc1700.util.field.JFloatField;
import lpc1700.util.field.JShortField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 29.10.2007
 * Time: 14:25:39
 * Окно ввода и редактирования партий
 * <p/>
 * на стадии разработки
 */
public class DialogParty extends JDialog implements ActionListener
{
	private JShortField str_num;		//	Строка
	private JTextField melt;			//	Серия
	private JTextField nakladnaya;		//	Накладная
	private JTextField customer;		//	Заказчик
	private JTextField uchastok;		//	Участок
	private JComboBox grade;			//	Марка
	private JFloatField in_height;		//	Толщина сляба, м
	private JFloatField in_width;		//	Ширина сляба, м
	private JFloatField in_length;		//	Длина сляба, м
	private JFloatField f4a_height;		//	Толщина подката, м
	private JFloatField out_height;		//	Толщина полосы, м
	private JFloatField out_width;		//	Ширина полосы, м
	private JShortField strip_in;		//	Штук
	private JShortField strip_under;	//	Штук недокатано
	private JShortField strip_throw;	//	Штук выброшено
	private JShortField strip_out;		//	Штук прокатано
	private JButton del;
	private JButton save;

	private Party party;

	public DialogParty(String grades[])
	{
		super();
		Dimension d = new Dimension(500, 300);
		setMinimumSize(d);
		setSize(d);
		setModal(true);
		setTitle("Данные о партии");
		setLayout(new BorderLayout());

		//	Строка
		JPanel temp = new JPanel(new GridLayout(9, 2));
		temp.add(new JLabel("Строка"));
		str_num = new JShortField();
		temp.add(str_num);
		//	Плавка серия
		temp.add(new JLabel("Плавка серия"));
		melt = new JTextField("XXXXXX");
		temp.add(melt);
		//	Накладная
		temp.add(new JLabel("Накладная"));
		nakladnaya = new JTextField("XXXXXX");
		temp.add(nakladnaya);
		//	Заказчик
		temp.add(new JLabel("Заказчик"));
		customer = new JTextField("XXXXXX");
		temp.add(customer);
		// Участок
		temp.add(new JLabel("Участок"));
		uchastok = new JTextField("XXXX");
		temp.add(uchastok);
		//	Марка
		temp.add(new JLabel("Марка"));
		grade = new JComboBox(grades);
		temp.add(grade);
		//	Толщина сляба, м
		temp.add(new JLabel("Толщина сляба, м"));
		in_height = new JFloatField();
		temp.add(in_height);
		//	Ширина сляба, м
		temp.add(new JLabel("Ширина сляба, м"));
		in_width = new JFloatField();
		temp.add(in_width);
		//	Длина сляба, м
		temp.add(new JLabel("Длина сляба, м"));
		in_length = new JFloatField();
		temp.add(in_length);
		//	Толщина подката, м
		temp.add(new JLabel("Толщина подката, м"));
		f4a_height = new JFloatField();
		temp.add(f4a_height);
		//	Толщина полосы, м
		temp.add(new JLabel("Толщина полосы, м"));
		out_height = new JFloatField();
		temp.add(out_height);
		//	Ширина полосы, м
		temp.add(new JLabel("Ширина полосы, м"));
		out_width = new JFloatField();
		temp.add(out_width);
		//	Штук
		temp.add(new JLabel("Штук"));
		strip_in = new JShortField();
		temp.add(strip_in);
		//	Штук недокатано
		temp.add(new JLabel("Штук недокатано"));
		strip_under = new JShortField();
		strip_under.setEnabled(false);
		temp.add(strip_under);
		//	Штук выброшено
		temp.add(new JLabel("Штук выброшено"));
		strip_throw = new JShortField();
		temp.add(strip_throw);
		//	Штук прокатано
		temp.add(new JLabel("Штук прокатано"));
		strip_out = new JShortField();
		strip_out.setEnabled(false);
		temp.add(strip_out);
		add(temp, BorderLayout.CENTER);

		temp = new JPanel();
		del = new JButton("Удалить");
		del.addActionListener(this);
		temp.add(del);
		save = new JButton("Сохранить");
		save.addActionListener(this);
		temp.add(save);
		add(temp, BorderLayout.SOUTH);
	}

	public void setParty(Party party)
	{
		if (party != null)
		{
			this.party = party;
			str_num.setValue(party.str_num);	//	Строка
			melt.setText(party.melt);			//	Плавка серия
			//! nakladnaya.setText(party.);	//	Накладная
			//! customer.setText(party.);	//	Заказчик
			//! uchastok.setText(party.uchastok);	// Участок
			// !!! grade;		//	Марка
			in_height.setValue(party.in_height);		//	Толщина сляба
			in_width.setValue(party.in_width);			//	Ширина сляба
			in_length.setValue(party.in_length);	//	Длина сляба, м
			f4a_height.setValue(party.f4a_height);		//	Толщина подката
			out_height.setValue(party.out_height);		//	Толщина полосы
			out_width.setValue(party.out_width);		//	Ширина полосы
			strip_in.setValue(party.strip_in);			//	Штук
			strip_under.setValue(party.strip_under);		//	Штук недокатано
			//	Штук выброшено
			strip_throw.setValue(party.strip_throw);
			//	Штук прокатано
			strip_out.setValue((short)party.getEndStrips());
		}
	}

	public void setGrades(String grades[])
	{
		if (grades != null && grades.length > 0)
		{
			grade.removeAllItems();
			for (int i = 0; i < grades.length; i++)
				grade.addItem(grades[i]);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (del == event)
		{
			party.setEndStrips();
			setVisible(false);
		} else if (save == event)
		{
			//	Строка
			party.str_num = str_num.getValue();
			//	Плавка !!!серия
			party.melt = melt.getText();
			//	Накладная
			//!
			//	Заказчик
			//!
			// Участок
			//!
			//	Марка
			// !!! grade;
			//	Толщина сляба, м
			party.in_height = in_height.getValue();
			//	Ширина сляба, м
			party.in_width = in_width.getValue();
			//	Длина сляба, м
			party.in_length= in_length.getValue();
			//	Толщина подката, м
			party.f4a_height = f4a_height.getValue();
			//	Толщина полосы, м
			party.out_height = out_height.getValue();
			//	Ширина полосы, м
			party.out_width = out_width.getValue();
			//	Штук
			party.strip_in = strip_in.getValue();
			//	Штук выброшено
			party.strip_throw = strip_throw.getValue();
			setVisible(false);
		}
	}

	public static void main(String arg[])
	{
		DialogParty d = new DialogParty(new String[]{"1", "2", "3"});
		d.setVisible(true);
	}
}
