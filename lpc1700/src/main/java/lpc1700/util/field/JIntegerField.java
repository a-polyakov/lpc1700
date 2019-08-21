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
 * Поле для ввода данных типа int
 */
public class JIntegerField extends JTextField implements CaretListener, KeyListener
{
	private int value;

	public JIntegerField()
	{
		this(0);
	}

	public JIntegerField(int value)
	{
		super();
		setValue(value);
		addCaretListener(this);
		addKeyListener(this);
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
		setText(String.valueOf(value));
	}

	public void caretUpdate(CaretEvent e)
	{
		try
		{
			value = Integer.parseInt(getText());
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
