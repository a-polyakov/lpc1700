package lpc1700.okna;

import lpc1700.stan.KletChistovaya;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 28.11.2007
 * Time: 13:52:58
 */
public class PanelRollsKlet extends JPanel implements MouseListener
{
	private KletChistovaya klet;
	private boolean in;

	private DialogRolls dialogRolls;

	public PanelRollsKlet(KletChistovaya klet, DialogRolls dialogRolls)
	{
		this.klet = klet;
		this.dialogRolls=dialogRolls;
		addMouseListener(this);
		in = false;
	}

	public void paint(Graphics g)
	{
		int w = getWidth();
		int h = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);

		if (in)
		{
			g.setColor(Color.decode("0x8494ff"));
			g.fillRoundRect(0,0,w-1,h-1,15,30);
		}
		w /= 2;
		h /= 6;
		float f;
		f = (klet.rollTop.diametr - klet.rollTop.d_min) / (klet.rollTop.d_max - klet.rollTop.d_min);
		if (f > 1) f = 1;
		else if (f < 0) f = 0;
		g.setColor(new Color(1 - f, f, 0));
		g.fillOval(w - h, 0, h * 2, h * 2);
		f = (klet.rollTopWork.diametr - klet.rollTopWork.d_min) / (klet.rollTopWork.d_max - klet.rollTopWork.d_min);
		if (f > 1) f = 1;
		else if (f < 0) f = 0;
		g.setColor(new Color(1 - f, f, 0));
		g.fillOval(w - h / 2, h * 2, h, h);
		f = (klet.rollBottomWork.diametr - klet.rollBottomWork.d_min) / (klet.rollBottomWork.d_max - klet.rollBottomWork.d_min);
		if (f > 1) f = 1;
		else if (f < 0) f = 0;
		g.setColor(new Color(1 - f, f, 0));
		g.fillOval(w - h / 2, h * 3, h, h);
		f = (klet.rollBottom.diametr - klet.rollBottom.d_min) / (klet.rollBottom.d_max - klet.rollBottom.d_min);
		if (f > 1) f = 1;
		else if (f < 0) f = 0;
		g.setColor(new Color(1 - f, f, 0));
		g.fillOval(w - h, h * 4, h * 2, h * 2);
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(h / 2.5f));
		g.drawString(klet.name, 3, h / 3 + 3);
		g.drawString(String.valueOf(klet.rollTop.diametr), w - h / 2, h + h / 6);
		g.drawString(String.valueOf(klet.rollTopWork.diametr), w - h / 2, h * 5 / 2 + h / 6);
		g.drawString(String.valueOf(klet.rollBottomWork.diametr), w - h / 2, h * 7 / 2 + h / 6);
		g.drawString(String.valueOf(klet.rollBottom.diametr), w - h / 2, h * 5 + h / 6);
	}

	public void mouseClicked(MouseEvent e)
	{
		dialogRolls.setKlet(klet);
		dialogRolls.setVisible(true);
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
		in = true;
		repaint();
	}

	public void mouseExited(MouseEvent e)
	{
		in = false;
		repaint();
	}
}
