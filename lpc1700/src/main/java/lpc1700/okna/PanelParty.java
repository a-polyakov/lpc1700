package lpc1700.okna;

import lpc1700.Stan;
import lpc1700.stan.Party;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 04.10.2007
 * Time: 12:43:48
 * Панель парний слябов
 * <p/>
 * возможность просмотра истории партий за другие дни и возврат к текущему списку
 * <p/>
 * при удалении партии браковать все её штуки а ести были штуки в ней прокатаные перенести их к следующей партии
 * для штуки показывать её текущие пораметры (длина ширина толщина время поступления статус(прокатка клеть 4а || готово [time] || удален после 4а))
 * выделяя партию выделять первую штуку этой партии
 * обратный поряток в истории
 */
public class PanelParty extends JSplitPane implements ActionListener, MouseListener
{
	private JButton addButton;
	private JButton editButton;
	private JButton history;
	private JTable tablePartiy;
	private JTable tableStrip;
	private JButton delSlaybButton;
	private JButton selSlaybButton;

	private DialogParty dialogParty;
	private FramePartyHistory framePartyHistory;
	private Stan stan;

	public PanelParty(Stan stan)
	{
		super(JSplitPane.VERTICAL_SPLIT);
		this.stan = stan;
		setDividerSize(8);
		setOneTouchExpandable(true);
		setDividerLocation(300);
		JPanel partiya = new JPanel(new BorderLayout());
		JPanel top = new JPanel();
		addButton = new JButton("Новая партия");
		addButton.addActionListener(this);
		top.add(addButton);
		editButton = new JButton("Редактировать");
		editButton.addActionListener(this);
		top.add(editButton);
		history = new JButton("Испория прокатки");
		history.addActionListener(this);
		top.add(history);
		partiya.add(top, BorderLayout.NORTH);
		tablePartiy = new JTable(stan.parties);
		tablePartiy.setColumnSelectionAllowed(true);
		tablePartiy.setRowSelectionAllowed(true);
		tablePartiy.addMouseListener(this);
		partiya.add(new JScrollPane(tablePartiy), BorderLayout.CENTER);
		setTopComponent(partiya);

		JPanel polosa = new JPanel(new BorderLayout());
		tableStrip = new JTable(stan.strips);
		tableStrip.setColumnSelectionAllowed(true);
		tableStrip.setRowSelectionAllowed(true);
		polosa.add(new JScrollPane(tableStrip), BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		delSlaybButton = new JButton("Удалить сляб");
		delSlaybButton.setEnabled(false);
		bottom.add(delSlaybButton);
		selSlaybButton = new JButton("Пометить как недокатаный");
		selSlaybButton.setEnabled(false);
		bottom.add(selSlaybButton);
		polosa.add(bottom, BorderLayout.SOUTH);
		setBottomComponent(polosa);
		dialogParty = new DialogParty(stan.grades.getNames());
		framePartyHistory = new FramePartyHistory();
	}

	// событие нажатие на кнопку
	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (addButton == event)
		{
			stan.parties.add(new Party(stan.posProcent.getPosProcent()));
			tablePartiy.setRowSelectionInterval(0, 0);
			tablePartiy.setColumnSelectionInterval(0, tablePartiy.getColumnCount() - 1);
			dialogParty.setParty(stan.parties.getParty(0));
			dialogParty.setVisible(true);
		} else if (editButton == event)
		{
			int selParty;
			selParty = tablePartiy.getSelectedRow();
			if (selParty >= 0)
			{
				tablePartiy.setColumnSelectionInterval(0, tablePartiy.getColumnCount() - 1);
				dialogParty.setParty(stan.parties.getParty(selParty));
				dialogParty.setVisible(true);
			}
		} else if (history == event)
		{
			framePartyHistory.setVisible(true);
		}
	}

	// события мыши
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{	// Выделить полосы выделеной партии
			int selParties;
			selParties = tablePartiy.getSelectedRow();
			tablePartiy.setColumnSelectionInterval(0, tablePartiy.getColumnCount() - 1);
			int selStrips;
			selStrips = stan.strips.selectParty(stan.parties.getParty(selParties));

			if (selStrips >= 0 && stan.parties.getParty(selParties).getOutStrip() > 0)
			{
				tableStrip.setColumnSelectionInterval(0, tableStrip.getColumnCount() - 1);
				tableStrip.setRowSelectionInterval(selStrips, selStrips + stan.parties.getParty(selParties).getOutStrip() - 1); //! bed strips
			} else
				tableStrip.clearSelection();
		}
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}
}
