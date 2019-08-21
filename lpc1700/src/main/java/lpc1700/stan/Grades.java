package lpc1700.stan;

import lpc1700.util.xml.XMLAttribute;
import lpc1700.util.xml.XMLElement;
import lpc1700.util.xml.XMLInterface;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 10.10.2007
 * Time: 14:03:56
 * Таблица марок стали
 */
public class Grades implements TableModel, XMLInterface
{
	private String title[];
	private Grade arrayGrade[];

	private int row;		// последняя запись

	protected EventListenerList listenerList = new EventListenerList();

	public Grades()
	{
		arrayGrade = new Grade[10];
		row = -1;
		title = new String[]{"Название", "Теплоемкость", "a"};
	}

	// заполняет вектор марками по умолчанию
	public void init()
	{
		arrayGrade = new Grade[]
				{
						new Grade("11 - Ст 1кп"),
						new Grade("21 - Ст 2кп"),
						new Grade("22 - Ст 2пс"),
						new Grade("23 - Ст 2сп"),
						new Grade("31 - Ст 3кп"),
						new Grade("32 - Ст 3пс"),
						new Grade("33 - Ст 3сп"),
						new Grade("34 - Ст 3Гпс"),
						new Grade("35 - Ст 3Гсп"),
						new Grade("81 - 08кп"),
						new Grade("82 - 08пс"),
						new Grade("103 - 10"),
						new Grade("151 - 15кп"),
						new Grade("203 - 20"),
						new Grade("80 - 08Ю"),
						new Grade("612 - 09Г2С"),
						new Grade("623 - 17ГС"),
						new Grade(" - Судосталь А"),
						new Grade(" - Судосталь В"),
						new Grade("9946 - S235JR"),
						new Grade("9944 - S275JR"),
						new Grade(" - A36"),
						new Grade("9938 - SAE 1006"),
						new Grade("9936 - SAE 1008"),
						new Grade("9940 - SAE 1010"),

						new Grade("1 - ST0"),
						new Grade("8 - A"),
						new Grade("12 - ST1PS"),
						new Grade("13 - ST1SP"),
						new Grade("41 - ST4KP"),
						new Grade("42 - ST4PS"),
						new Grade("43 - ST4SP"),
						new Grade("52 - ST5PS"),
						new Grade("53 - ST5SP"),
						new Grade("101 - 10KP"),
						new Grade("102 - 10PS"),
						new Grade("150 - 15UA"),
						new Grade("152 - 15PS"),
						new Grade("153 - 15"),
						new Grade("160 - 18UA"),
						new Grade("202 - 20PS"),
						new Grade("253 - 25"),
						new Grade("353 - 35"),
						new Grade("403 - 40"),
						new Grade("453 - 45"),
						new Grade("503 - 50"),
						new Grade("553 - 55"),
						new Grade("611 - 09G2"),
						new Grade("613 - 09G2D"),
						new Grade("614 - 09G2SD"),
						new Grade("615 - 10G2C1"),
						new Grade("616 - 10G2C1D"),
						new Grade("617 - 10NSND"),
						new Grade("618 - 10HNDP"),
						new Grade("619 - 12GS"),
						new Grade("620 - 14G2"),
						new Grade("621 - 14NGS"),
						new Grade("622 - 16GS"),
						new Grade("624 - 17G1S"),
						new Grade("840 - A40"),
						new Grade("1008 - C1008"),
						new Grade("1032 - RSD32"),
						new Grade("1100 - 20H4GMFA"),
						new Grade("1101 - 20H4MFB"),
						new Grade("1107 - MARKA7"),
						new Grade("1140 - E40"),
						new Grade("1141 - 09G2"),
						new Grade("9035 - ST50-3"),
						new Grade("9836 - ASTMA36"),
						new Grade("9932 - RST37-2"),
						new Grade("9933 - UST37-2"),
						new Grade("9934 - RRST37-2"),
						new Grade("9935 - RRST52-3"),
						new Grade("9937 - S235JRG1"),
						new Grade("9939 - S235JRG2"),
						new Grade("9943 - S235"),
						new Grade("9945 - s355JR"),
						new Grade("9947 - s275J2G3"),
						new Grade("9948 - s355J2"),
						new Grade("9949 - s355JOH")
				};
		row = arrayGrade.length - 1;
	}

	// ! insert добавить строку
	public void addRow()
	{
		Grade grade = new Grade("new");
		insertRow(row + 1, grade);
	}

	public void insertRow(int rowIndex, Grade grade)
	{
		if (rowIndex >= 0 && rowIndex <= row + 1)
		{
			int i;
			boolean find = false;
			if (find(grade) < 0)
			{	// такой записи ещё не было добавляем
				row++;
				if (row + 1 > arrayGrade.length)
					setSize(row + 10);
				for (i = row; i > rowIndex; i--)
				{
					arrayGrade[i] = arrayGrade[i - 1];
				}
				arrayGrade[rowIndex] = grade;
				fireTableChanged(new TableModelEvent(this));
			}
		}
	}

	// поиск с конца
	private int find(Grade grade)
	{
		int i;
		for (i = row; i >= 0 && !arrayGrade[i].equals(grade); i--) ;
		return i;
	}

	// удалить строку
	public void delRow(int rowIndex)
	{
		if (rowIndex >= 0 && rowIndex <= row)
		{
			int i;
			for (i = rowIndex; i < row; i++)
				arrayGrade[i] = arrayGrade[i + 1];
			row--;
			fireTableChanged(new TableModelEvent(this));
		}
	}

	// количество строк
	public int getRowCount()
	{
		return row + 1;
	}

	// количстко столбцов
	public int getColumnCount()
	{
		return title.length;
	}

	// название поля
	public String getColumnName(int columnIndex)
	{
		if (columnIndex >= 0 && columnIndex < title.length)
			return title[columnIndex];
		else
			return null;
	}

	public Class<?> getColumnClass(int columnIndex)
	{

		switch (columnIndex)
		{
			case 0:
				return String.class;
			case 1:
				return Float.class;
			case 2:
				return Float.class;
			default:
				return Object.class;
		}
	}

	// функция возвращяет возможно ли редактировать поле
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}

	// получить значение поля
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex >= 0 && rowIndex <= row)
		{
			switch (columnIndex)
			{
				case 0:
					return arrayGrade[rowIndex].name;
				case 1:
					return arrayGrade[rowIndex].q;
				case 2:
					return arrayGrade[rowIndex].t;
				default:
					return null;
			}
		} else
			return null;
	}

	// имя столбца
	public String[] getNames()
	{
		String temp[] = new String[row + 1];
		for (int i = 0; i < temp.length; i++)
			temp[i] = arrayGrade[i].name;
		return temp;
	}

	// задать значение поля
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (rowIndex >= 0 && rowIndex <= row)
		{
			switch (columnIndex)
			{
				case 0:
					arrayGrade[rowIndex].name = (String) aValue;
					break;
				case 1:
					arrayGrade[rowIndex].q = (Float) aValue;
					break;
				case 2:
					arrayGrade[rowIndex].t = (Float) aValue;
					break;
			}
		}
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
		// Guaranteed to return a non-null array
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

	// изменить размер массивов
	private void setSize(int newSize)
	{
		if (newSize > 0)
			arrayGrade = Arrays.copyOf(arrayGrade, newSize);
	}

	// Загрузка таблицы марок стали
	/*!!!public void load(XMLStreamReader xml) throws XMLStreamException
	{
		boolean error = false;
		if (xml.getLocalName().equals("Grades"))
		{
			xml.next();
			if (xml.getLocalName().equals("Grades"))
				error = true;
			else
				while (xml.getLocalName().equals("metal"))
				{
					insertRow(row + 1, new Grade(xml.getAttributeValue(0)));
					xml.next(); // end metal
					xml.next();
				}
			xml.next();
		} else
			error = true;

		if (error || row < 0)
			init();
	}
	*/

	public void readFromXMLElement(XMLElement element)
	{
		if (element != null)
		{
			int l = element.getCountElements();
			if (l > 0)
			{
				XMLElement temp;
				for (int i = 0; i < l; i++)
				{
					temp = element.getElement(i);
					if ("metal".equals(temp.getName()))
					{
						insertRow(row + 1, new Grade(temp.getAttribute(0).getValue()));
						// !! другие параметры
					}
				}
			} else
				init();
		} else
			init();
	}

	// !!! Сохранение таблицы марок стали
	public XMLElement writeToXMLElement(String name)
	{
		XMLElement element = new XMLElement(name);
		XMLElement temp;
		int i, j;
		for (i = 0; i <= row; i++)
		{
			temp = new XMLElement("metal");
			for (j = 0; j < getColumnCount(); j++)
				temp.addAttribute(new XMLAttribute(getColumnName(j), getValueAt(i, j).toString()));
			element.addElement(temp);
		}
		return element;
	}
}