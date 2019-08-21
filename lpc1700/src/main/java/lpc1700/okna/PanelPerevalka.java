package lpc1700.okna;

import lpc1700.Stan;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 12.10.2007
 * Time: 9:21:43
 * ! панель перевалка с информацией о волках
 * !!!!
 */
public class PanelPerevalka extends JPanel
{
	private JButton f0_roll;
	private JButton f1_roll;
	private JButton f2_roll;
	private JButton f3_roll;
	private JButton f4_roll;
	private JButton f4a_roll;

	private PanelRollsKlet roll_f5;
	private PanelRollsKlet roll_f6;
	private PanelRollsKlet roll_f7;
	private PanelRollsKlet roll_f8;
	private PanelRollsKlet roll_f9;
	private PanelRollsKlet roll_f10;

	private DialogRolls dialogRolls;
	private Stan stan;

	public PanelPerevalka(Stan stan)
	{
		super(new GridLayout(2, 1));
		this.stan = stan;
		dialogRolls = new DialogRolls();

		JPanel panTop = new JPanel(new GridLayout(1, 6));

		f0_roll = new JButton("F0");
		panTop.add(f0_roll);
		f1_roll = new JButton("F1");
		panTop.add(f1_roll);
		f2_roll = new JButton("F2");
		panTop.add(f2_roll);
		f3_roll = new JButton("F3");
		panTop.add(f3_roll);
		f4_roll = new JButton("F4");
		panTop.add(f4_roll);
		f4a_roll = new JButton("F4a");
		panTop.add(f4a_roll);
		add(panTop);

		JPanel panBottom = new JPanel(new GridLayout(1, 6));
		roll_f5 = new PanelRollsKlet(stan.F5,dialogRolls);
		panBottom.add(roll_f5);
		roll_f6 = new PanelRollsKlet(stan.F6,dialogRolls);
		panBottom.add(roll_f6);
		roll_f7 = new PanelRollsKlet(stan.F7,dialogRolls);
		panBottom.add(roll_f7);
		roll_f8 = new PanelRollsKlet(stan.F8,dialogRolls);
		panBottom.add(roll_f8);
		roll_f9 = new PanelRollsKlet(stan.F9,dialogRolls);
		panBottom.add(roll_f9);
		roll_f10 = new PanelRollsKlet(stan.F10,dialogRolls);
		panBottom.add(roll_f10);
		add(panBottom);
	}
}
