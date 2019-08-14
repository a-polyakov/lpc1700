package lpc1700.okna;

import lpc1700.Index;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 03.10.2007
 * Time: 13:25:14
 * Панель марок метала
 * <p/>
 * * для марки стали
 * - теплоёмкость металла 581
 * - коэффициент линейного расширения металла
 * - коэффициент теплоотдачи (воздух валки вода)
 * - изменение температуры при дефорации
 * - плотность металла от температуры
 * - коэффициент трения
 */
public class PanelGrade extends JPanel implements ActionListener
{
	private Index index;
	private JButton addGrade;
	private JButton delGrade;
	private JButton saveGrade;
	private JTable table;

	public PanelGrade(Index index)
	{
		super(new BorderLayout());
		JPanel p = new JPanel();
		addGrade = new JButton("Добавить");
		addGrade.addActionListener(this);
		p.add(addGrade);
		delGrade = new JButton("Удалить");
		delGrade.addActionListener(this);
		p.add(delGrade);
		saveGrade = new JButton("Сохранить изменениея");
		saveGrade.setEnabled(false);
		saveGrade.addActionListener(this);
		p.add(saveGrade);
		add(p, BorderLayout.NORTH);
		table = new JTable(index.stan.grades);
		add(new JScrollPane(table), BorderLayout.CENTER);
		this.index = index;
	}

	public void actionPerformed(ActionEvent e)
	{
		Object event = e.getSource();
		if (addGrade == event)
		{
			index.stan.grades.addRow();
			int i = index.stan.grades.getRowCount() - 1;
			table.setRowSelectionInterval(i, i);
		} else if (delGrade == event)
		{
			int selRow[] = table.getSelectedRows();
			int i;
			for (i = 0; i < selRow.length; i++)
				index.stan.grades.delRow(selRow[i] - i);
		} else if (saveGrade == event)
		{
		}
	}
}
