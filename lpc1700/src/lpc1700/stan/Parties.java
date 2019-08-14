package lpc1700.stan;

import lpc1700.Smena;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 16.10.2007
 * Time: 14:56:05
 * Список партий
 */
public class Parties implements TableModel
{
	private int current;
	private int row;
	private Party arrayParty[];
	private String title[];

	private Vector arrayStrip;

	protected EventListenerList listenerList = new EventListenerList();

	public Parties()
	{
		arrayParty = new Party[10];
		current = 0;
		row = -1;
		title = new String[]
				{
						"Строка",
						"Плавка серия",
						"Введено",
						"Марка",
						"Штук",
						"Недокаты",
						"Выбросы",
						"Сляб H W L",
						"Подкат H",
						"Полоса H W"
				};
		arrayStrip = new Vector();
	}

	// добавить сведения о партии
	public void add(Party party)
	{
		if (party != null)
		{
			// если такой партии еще не было: сохраняем её
			if (indexOf(party) < 0)
			{
				row++;
				if (row >= arrayParty.length)
					setSize(arrayParty.length + 10);
				arrayParty[row] = party;
				// если есть не известные слябы
				if (!arrayStrip.isEmpty())
				{
					int i;
					boolean f = true;
					while (f && arrayStrip.size() > 0)
						f = connect((Strip) arrayStrip.remove(0));	// связать с партией и удалить из списка
				}
				// отображение изменений
				fireTableChanged(new TableModelEvent(this));
			}
		}
	}

	public Party getParty(int index)
	{
		if (index >= 0 && index <= row)
			return arrayParty[row - index];
		else
			return null;
	}

	// связать партию со штукой
	public boolean connect(Strip strip)
	{
		while (current <= row && arrayParty[current].finish())
			current++;	// перейти на следующюю партию
		if (current > row)
		{	// нет данных о партии
			arrayStrip.addElement(strip);
			return false;
		} else
		{	// ещё одна штука партии
			arrayParty[current].setNextStrip(strip);
			return true;
		}
	}

	// количество строк
	public int getRowCount()
	{
		return row + 1;
	}

	// количество столбцов
	public int getColumnCount()
	{
		return title.length;
	}

	// имя столбца
	public String getColumnName(int columnIndex)
	{
		if (columnIndex >= 0 && columnIndex < title.length)
			return title[columnIndex];
		else
			return null;
	}

	// Класс слолбца
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:	// Строка
				return Short.class;
			case 1:	// Плавка серия
				return String.class;
			case 2:	// Введено
				return String.class;
			case 3:	// Марка
				return String.class;
			case 4:	// Штук
				return String.class;
			case 5:	// Недокаты
				return Short.class;
			case 6:	// Выбросы
				return Short.class;
			case 7:	// Сляб H W L
				return String.class;
			case 8:	// Подкат H
				return Float.class;
			case 9:	// Полоса H W
				return String.class;
			default:
				return Object.class;
		}
	}

	// функция возвращяет возможно ли редактировать поле
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false; // запрет изменения данных в таблице
	}

	// получить значение ячейки
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex >= 0 && rowIndex <= row)
			switch (columnIndex)
			{
				case 0:	// Строка
					return arrayParty[row - rowIndex].str_num;
				case 1:	// Плавка серия
					return arrayParty[row - rowIndex].melt + " " + arrayParty[row - rowIndex].serial;
				case 2:	// Введено
					return new Smena(arrayParty[row - rowIndex].time_enter).toStringHHMMSS();
				case 3:	// Марка
					return arrayParty[row - rowIndex].grade;
				case 4:	// Штук
					return arrayParty[row - rowIndex].strip_in + "/" + arrayParty[row - rowIndex].getEndStrips();
				case 5:	// Недокаты
					return arrayParty[row - rowIndex].strip_under;
				case 6:	// Выбросы
					return arrayParty[row - rowIndex].strip_throw;
				case 7:	// Сляб H W L
					return arrayParty[row - rowIndex].in_height + " X " +
							arrayParty[row - rowIndex].in_width + " X " +
							arrayParty[row - rowIndex].in_length;
				case 8:	// Подкат H
					return arrayParty[row - rowIndex].f4a_height;
				case 9:	// Полоса H W
					return arrayParty[row - rowIndex].out_height + " X " +
							arrayParty[row - rowIndex].out_width;
				default:
					return null;
			}
		else
			return null;
	}

	// задать значение ячейки
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		// редактировать запрещено
	}

	public void addTableModelListener(TableModelListener l)
	{
		listenerList.add(TableModelListener.class, l);
	}

	public void removeTableModelListener(TableModelListener l)
	{
		listenerList.remove(TableModelListener.class, l);
	}

	public void fireTableChanged(TableModelEvent e)
	{
		// Guaranteed to return a non-null arrayParty
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TableModelListener.class)
			{
				((TableModelListener) listeners[i + 1]).tableChanged(e);
			}
		}
	}

	// поиск с конца
	public int indexOf(Party party)
	{
		int i;
		for (i = row; i >= 0 && !arrayParty[i].equals(party); i--) ;
		return i;
	}

	// задать размер массива
	private void setSize(int newLength)
	{
		if (newLength > 0 && newLength != arrayParty.length)
			arrayParty = Arrays.copyOf(arrayParty, newLength);
	}

	// !!! сохранение сведений о партиях
	public void save(OutputStream out)
	{
		int i;
		for (i = 0; i <= row; i++)
			arrayParty[i].save(out);
	}
	// !!!
	public void load(InputStream in) throws IOException
	{
		Party party;
		while (in.available()>0)
		{
			party=new Party(in);
			add(party);
		}
	}
}
