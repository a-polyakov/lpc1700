package lpc1700.stan;

import lpc1700.Smena;
import lpc1700.Stan;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 18.10.2007
 * Time: 7:34:40
 * Список полос
 */
public class Strips implements TableModel
{
	private int row;
	private Strip arrayStrip[];
	private String title[];

	protected EventListenerList listenerList = new EventListenerList();

	public Strips()
	{
		arrayStrip = new Strip[10];
		row = -1;
		title = new String[]
				{
						"№",
						"Длина",
						"Ширина",
						"Толщина",
						"Поступил",
						"Статус"
				};
	}

	// добавить сведения о партии
	public void add(Strip strip)
	{
		if (strip != null)
		{
			row++;
			if (row >= arrayStrip.length)
				setSize(arrayStrip.length + 10);
			arrayStrip[row] = strip;
			// отображение изменений
			fireTableChanged(new TableModelEvent(this));
		}
	}

	// первая штука партии
	public int selectParty(Party arrayParty)
	{
		if (arrayParty != null)
		{
			int i;
			for (i = row; i >= 0; i--)// && j<arrayParty.length; i--)
				if (arrayStrip[i].getParty() == arrayParty)
					return row - i;
		}
		return -1;
	}

	// задать размер массива
	private void setSize(int newLength)
	{
		if (newLength > 0 && newLength != arrayStrip.length)
			arrayStrip = Arrays.copyOf(arrayStrip, newLength);
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
			case 0:
				return Short.class;
			case 1:
			case 2:
			case 3:
				return Float.class;
			case 4:
			case 5:
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
				case 0:
					return arrayStrip[row - rowIndex].getID();
				case 1:
					return arrayStrip[row - rowIndex].getLength();
				case 2:
					return arrayStrip[row - rowIndex].getWinth();
				case 3:
					return arrayStrip[row - rowIndex].getHeight();
				case 4:
					return new Smena(arrayStrip[row - rowIndex].timeStart).toStringHHMMSS();
				case 5:
					return arrayStrip[row - rowIndex].getStatus();
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

	// найти полосу находящююсь под меткой
	public Strip findStripIn(float location)
	{
		int i;
		for (i = row; i >= 0; i--)
			if (arrayStrip[i].end() <= location)
			{	// конец полосы до участка
				if (arrayStrip[i].head >= location)
					// начало полопы за участком
					return arrayStrip[i];	// участок на полосе
			} else
				// конец полосы за участком
				i = -1;
		return null;
	}

	// !!! найти полосу находящююсь под меткой или скоро там окажется
	// ! скорость на промежуточном рольганге может меняться
	// ! не раньшн ем на максимальной скорости
	// ! не позже максимального времени ожидания
	public Strip findStripTo(float location)
	{
		int i;
		for (i = row; i > 0 && arrayStrip[i - 1].head < location; i--) ;
		// !! if (arrayStrip[i].head<location+3 && arrayStrip[i].head + (System.currentTimeMillis() - arrayStrip[i].timeEnd) * arrayStrip[i].getSpeed(location) >= location)
		// полоса могла за пройденое время дойти до метки
		if (i >= 0 && arrayStrip[i].head + (Stan.timeMaster.toLong() - arrayStrip[i].timeEnd) * arrayStrip[i].getSpeed(location) / 1000 >= location)
			return arrayStrip[i];
		else
			return null;
	}

	// !!!!
	public void save(OutputStream out) throws IOException
	{
		for (int i = 0; i <= row; i++)
			arrayStrip[i].save(out);
	}

	// !!!
	public void load(InputStream in) throws IOException
	{
		Strip strip;
		while (in.available()>0)
		{
			strip=new Strip(in);
			add(strip);
		}
	}
}
