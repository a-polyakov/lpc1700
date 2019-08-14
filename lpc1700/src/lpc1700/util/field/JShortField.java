package lpc1700.util.field;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 19.11.2007
 * Time: 9:38:02
 * Поле для ввода данных типа short
 */
public class JShortField extends JTextField implements CaretListener, KeyListener
{
	private short value;

	public JShortField()
	{
		this((short) 0);
	}

	public JShortField(short value)
	{
		super();
		setValue(value);
		addCaretListener(this);
		addKeyListener(this);
	}

	public short getValue()
	{
		return value;
	}

	public void setValue(short value)
	{
		this.value = value;
		setText(String.valueOf(value));
	}

	public void caretUpdate(CaretEvent e)
	{
		try
		{
			value = Short.parseShort(getText());
			setBackground(Color.WHITE);
		}
		catch (Exception ee)
		{
			setBackground(new Color(255, 200, 220));
		}
	}

	public void keyTyped(KeyEvent e)
	{
	}

	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_DOWN)
		{
			value--;
			setText(String.valueOf(value));
		} else if (key == KeyEvent.VK_UP)
		{
			value++;
			setText(String.valueOf(value));
		}
	}

	public void keyReleased(KeyEvent e)
	{
	}
}
