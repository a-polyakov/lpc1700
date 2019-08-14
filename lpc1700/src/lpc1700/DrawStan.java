package lpc1700;

import lpc1700.stan.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.MemoryImageSource;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 13.09.2007
 * Time: 8:52:33
 * Прорисовка стана
 * <p/>
 * петледержатель (работа, охлаждение)
 * охложденеи клетей
 * работа винтов клети
 * моталки
 * толщиномер
 */
public class DrawStan extends JPanel implements ComponentListener
{
	private Stan stan;

	private int arrayCos[];
	private int arraySin[];
	private int arrayCosI[];
	private int arraySinI[];
	private int oldW, oldH;
	private float scale;

	public DrawStan(Stan stan)
	{
		this.stan = stan;
		arrayCos = new int[7];
		arraySin = new int[7];
		arrayCosI = new int[5];
		arraySinI = new int[5];
		double a;
		for (int i = 0; i < 7; i++)
		{
			a = Math.toRadians(45 * i - 45);
			arrayCos[i] = (int) (25 * Math.cos(a));
			arraySin[i] = (int) (25 * Math.sin(a));
		}
		for (int i = 0; i < 5; i++)
		{
			a = Math.toRadians(45 * i - 180);
			arrayCosI[i] = (int) (23 * Math.cos(a));
			arraySinI[i] = (int) (16 * Math.sin(a));
		}
		addComponentListener(this);
		oldW = 787;
		oldH = 545;
		scale = 1;
	}

	public void paint(Graphics g)
	{
		// очистка экрана
		g.setColor(new Color(0xffeeeeee));
		g.fillRect(0, 0, oldW, oldH);
		// прорисовка стана
		drawIn(g, 10, 10, stan);
		drawBits(g, 10, 70, stan.temp);
		drawKlet(g, 80, 10, scale, stan.klet0);
		drawKlet(g, 180, 10, scale, stan.klet1);
		drawKlet(g, 280, 10, scale, stan.klet2);
		drawKlet(g, 380, 10, scale, stan.klet3);
		drawKlet(g, 480, 10, scale, stan.klet4);
		drawKlet(g, 580, 10, scale, stan.klet4a);
		drawPyrometr(g, 650, 50, scale, stan.t1);
		drawPyrometr(g, 430, 225, scale, stan.t2);
		drawNognici(g, 550, 280, scale, stan.nognici);
		drawPyrometr(g, 630, 225, scale, stan.t3);
		drawGidrozbiv(g, 690, 285, scale);
		drawKlet(g, 15, 290, scale, stan.F5);
		drawLoop(g, 75, 470, scale, stan.loop5_6);
		drawKlet(g, 115, 290, scale, stan.F6);
		drawLoop(g, 175, 470, scale, stan.loop6_7);
		drawKlet(g, 215, 290, scale, stan.F7);
		drawLoop(g, 275, 470, scale, stan.loop7_8);
		drawKlet(g, 315, 290, scale, stan.F8);
		drawLoop(g, 375, 470, scale, stan.loop8_9);
		drawKlet(g, 415, 290, scale, stan.F9);
		drawLoop(g, 475, 470, scale, stan.loop9_10);
		drawKlet(g, 515, 290, scale, stan.F10);
		drawPyrometr(g, 595, 390, scale, stan.t4);
		drawPyrometr(g, 625, 390, scale, stan.t5);
		drawThickness(g, 585, 480, scale, stan.thickness);
		drawPyrometr(g, 685, 390, scale, stan.t6);
		drawWind(g, 700, 460, scale, stan.wind_1);
		drawWind(g, 730, 460, scale, stan.wind_2);
		drawWind(g, 760, 460, scale, stan.wind_3);
	}

	private void drawIn(Graphics g, int x, int y, Stan stan)
	{
		if (stan.pech1)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);
		g.drawString("П1", x, y);
		if (stan.pech2)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);
		g.drawString("П2", x, y + 12);
		if (stan.pech3)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);
		g.drawString("П3", x, y + 24);
		g.drawString(((stan.tranzit) ? "Транзи+" : "Транзит-"), x, y + 36);
		g.drawString(((stan.start) ? "Start+" : "Start-"), x, y + 48);
	}

	// нарисовать пирометр по его значениям
	private void drawPyrometr(Graphics g, int x, int y, float scale, Pyrometer t)
	{
		int xs = (int) (x * scale);
		int xp2s = (int) ((x + 2) * scale);
		int xp8s = (int) ((x + 8) * scale);
		int xp10s = (int) ((x + 10) * scale);
		// размер
		//g.drawRect(xs,(int)(y*scale),(int)(40*scale),(int)(60*scale));
		if (!t.work)
			drawNotWork(g, x + 12, y, scale);
		g.setColor(Color.BLACK);
		g.drawArc(xs, (int) (y * scale), (int) (10 * scale), (int) (10 * scale), 0, 180);
		g.drawLine(xs, (int) ((y + 5) * scale), xs, (int) ((y + 45) * scale));
		g.drawLine(xp10s, (int) ((y + 5) * scale), xp10s, (int) ((y + 45) * scale));
		g.drawArc(xs, (int) ((y + 40) * scale), (int) (10 * scale), (int) (10 * scale), 180, 180);
		g.drawLine(xp2s, (int) ((y + 9) * scale), xp8s, (int) ((y + 9) * scale));
		g.drawLine(xp2s, (int) ((y + 18) * scale), xp8s, (int) ((y + 18) * scale));
		g.drawLine(xp2s, (int) ((y + 27) * scale), xp8s, (int) ((y + 27) * scale));
		g.drawLine(xp2s, (int) ((y + 36) * scale), xp8s, (int) ((y + 36) * scale));
		g.drawLine(xp2s, (int) ((y + 45) * scale), xp8s, (int) ((y + 45) * scale));

		g.drawLine(xp2s, (int) ((y + 48) * scale), xp2s, (int) ((y + 57) * scale));
		g.drawLine(xp8s, (int) ((y + 48) * scale), xp8s, (int) ((y + 57) * scale));
		g.drawArc(xp2s, (int) ((y + 55) * scale), (int) (6 * scale), (int) (5 * scale), 180, 180);
		g.drawString(String.valueOf(t.getT()), xp10s, (int) ((y + 60) * scale));

		g.setColor(Color.RED);
		if (t.sis())
		{	// вывести температуру
			int k = 45 * (t.getT() - t.min_t) / (t.max_t - t.min_t);
			g.fillRect((int) ((x + 4) * scale), (int) ((y + 45 - k) * scale), (int) (3 * scale), (int) ((5 + k) * scale));
		}
	}

	// !!! ножницы
	private void drawNognici(Graphics g, int x, int y, float scale, Nognici nognici)
	{
		int xp7s = (int) ((x + 7) * scale);
		int ys = (int) (y * scale);
		g.setColor(Color.BLACK);
		// размер
		//g.drawRect((int) (x * scale), ys, (int) (15 * scale), (int) (30 * scale));
		g.drawPolygon(new int[]{xp7s, xp7s, (int) ((x + 12) * scale)}, new int[]{ys, (int) ((y + 15) * scale), ys}, 3);
		g.drawRect((int) (x * scale), (int) ((y + 25) * scale), (int) (7 * scale), (int) (5 * scale));
	}

	private void drawGidrozbiv(Graphics g, int x, int y, float scale)
	{
		int xs = (int) (x * scale);
		int xp10s = (int) ((x + 10) * scale);
		int xp13s = (int) ((x + 13) * scale);
		int ys = (int) (y * scale);
		g.setColor(Color.BLUE);
		//g.drawRect(xs,ys,(int)(13*scale),(int)(25*scale));	// размер
		g.drawPolygon(new int[]{xs, xp10s, xp13s},
				new int[]{(int) ((y + 10) * scale), ys, (int) ((y + 4) * scale)}, 3);
		g.drawPolygon(new int[]{xs, xp10s, xp13s},
				new int[]{(int) ((y + 15) * scale), (int) ((y + 25) * scale), (int) ((y + 21) * scale)}, 3);
	}

	private void drawKlet(Graphics g, int x, int y, float scale, KletHidro klet)
	{
		int s60 = (int) (60 * scale);
		int xs = (int) (x * scale);
		int xp15s = (int) ((x + 15) * scale);
		int yp190s = (int) ((y + 190) * scale);
		if (klet.cool)
			drawNotCool(g, x + 2, y + 192, scale);
		if (!klet.work)
			drawNotWork(g, x + 52, y + 242, scale);
		if (klet.open)
			drawOpen(g, x, y + 188, scale);
		g.setColor(Color.BLACK);
		// размер
		//g.drawRect(xs, (int) (y * scale), s60, (int) (250 * scale));
		int hInt = klet.dr_pos + klet.op_pos - klet.pos_cyl_dr / 10 - klet.pos_cyl_op / 10;
		float h = (float) hInt / 200;
		hInt = hInt / 100;
		if (hInt > 100) hInt = 100;
		if (klet.sis())
		{
			int k = 30 * (klet.op_force + klet.dr_force - Klet.force_min) / (Klet.force_max - Klet.force_min);
			g.drawRect(xs, (int) ((y + 130 - k - hInt) * scale), s60, (int) (k * scale));
		}
		int yp130mhs = (int) ((y + 130 - hInt) * scale);
		g.drawString(String.valueOf(klet.dr_force + klet.op_force), xp15s, yp130mhs);
		g.drawOval(xs, yp130mhs, s60, s60);
		drawI(g, x, y + 130 - hInt, scale, klet.motor);
		g.drawString(String.valueOf(h), xp15s, (int) ((y + 185 - hInt) * scale));
		g.drawOval(xs, yp190s, s60, s60);
		drawSpeed(g, x, y + 190, scale, klet.motor);
	}

	private void drawKlet(Graphics g, int x, int y, float scale, KletChistovaya klet)
	{
		int s60 = (int) (60 * scale);
		int xs = (int) (x * scale);
		int xp15s = (int) ((x + 15) * scale);
		int yp190s = (int) ((y + 190) * scale);
		if (klet.cool)
			drawNotCool(g, x + 2, y + 192, scale);
		if (!klet.work)
			drawNotWork(g, x + 52, y + 242, scale);
		if (klet.open)
			drawOpen(g, x, y + 188, scale);
		g.setColor(Color.BLACK);
		// размер
		//g.drawRect(xs, (int) (y * scale), s60, (int) (250 * scale));
		int hInt = klet.dr_pos + klet.op_pos;
		float h = (float) hInt / 200;
		hInt = hInt / 100;
		if (hInt > 100) hInt = 100;
		if (klet.sis())
		{
			int k = 30 * (klet.op_force + klet.dr_force - Klet.force_min) / (Klet.force_max - Klet.force_min);
			g.drawRect(xs, (int) ((y + 130 - k - hInt) * scale), s60, (int) (k * scale));
		}
		int yp130mhs = (int) ((y + 130 - hInt) * scale);
		g.drawString(String.valueOf(klet.dr_force + klet.op_force), xp15s, yp130mhs);
		g.drawOval(xs, yp130mhs, s60, s60);
		drawI(g, x, y + 130 - hInt, scale, klet.motor);
		g.drawString(String.valueOf(h), xp15s, (int) ((y + 185 - hInt) * scale));
		g.drawOval(xs, yp190s, s60, s60);
		drawSpeed(g, x, y + 190, scale, klet.motor);
	}

	// клеть
	private void drawKlet(Graphics g, int x, int y, float scale, Klet klet)
	{
		int s60 = (int) (60 * scale);
		int xs = (int) (x * scale);
		int xp15s = (int) ((x + 15) * scale);
		if (!klet.work)
			drawNotWork(g, x + 51, y + 191, scale);
		if (klet.open)
			drawOpen(g, x, y + 188, scale);
		g.setColor(Color.BLACK);
		// размер
		//g.drawRect(xs, (int) (y * scale), s60, (int) (200 * scale));
		int hInt = klet.op_pos / 200;
		if (hInt > 50) hInt = 50;
		if (klet.sis())
		{
			int k = 30 * (klet.motor.P - Motor.P_MIN) / (Motor.P_MAX - Motor.P_MIN);
			g.drawRect(xs, (int) ((y + 80 - k - hInt) * scale), s60, (int) (k * scale));
		}
		int yp80mhs = (int) ((y + 80 - hInt) * scale);
		g.drawString(String.valueOf(klet.motor.P), xp15s, yp80mhs);
		g.drawOval(xs, yp80mhs, s60, s60);
		float h = (float) klet.op_pos / 100;
		g.drawString(String.valueOf(h), xp15s, (int) ((y + 135 - hInt) * scale));
		g.drawOval(xs, (int) ((y + 140) * scale), s60, s60);
	}

	// петледержатель
	private void drawLoop(Graphics g, int x, int y, float scale, Loop loop)
	{
		int s10 = (int) (10 * scale);
		int xs = (int) (x * scale);
		int xp5s = (int) ((x + 5) * scale);

		if (!loop.cool)
			drawNotCool(g, x + 27, y + 11, scale);
		if (!loop.work)
			drawNotWork(g, x + 20, y + 24, scale);
		g.setColor(Color.BLACK);
		// размер
		//g.drawRect(xs, (int) (y * scale), (int) (30 * scale), (int) (35 * scale));
		double alRadian = Math.toRadians(90 * (loop.pos - Loop.POS_MIN) / (Loop.POS_MAX - Loop.POS_MIN));
		double cos = Math.cos(alRadian);
		double sin = Math.sin(alRadian);

		g.drawLine(xp5s, (int) ((y + 20) * scale), (int) ((x + 10 * cos + 5) * scale), (int) ((y + 20 - 10 * sin) * scale));
		g.drawOval((int) ((x + 15 * cos) * scale), (int) ((y + 15 - 15 * sin) * scale), s10, s10);
		g.drawString(String.valueOf(loop.pos), xp5s, (int) ((y + 32) * scale));
	}

	// график толщиномера
	private void drawThickness(Graphics g, int x, int y, float scale, Thickness thickness)
	{
		int s15 = (int) (15 * scale);
		int s30 = (int) (30 * scale);
		int s45 = (int) (45 * scale);
		int s60 = (int) (60 * scale);
		int s200 = (int) (200 * scale);
		int xs = (int) (x * scale);
		int xp200s = (int) ((x + 200) * scale);
		int yp15s = (int) ((y + 15) * scale);
		int yp30s = (int) ((y + 30) * scale);
		int yp45s = (int) ((y + 45) * scale);
		int yp60s = (int) ((y + 60) * scale);
		int imageInt[] = new int[s200 * s60];

		int i, j = s200 * (s60 - 1);
		// размер
		for (i = 0; i < s200; i++)
		{
			imageInt[i] = 0xff000000;
			imageInt[j + i] = 0xff000000;
		}
		for (i = s200; i <= j; i += s200)
		{
			imageInt[i - 1] = 0xff000000;
			imageInt[i] = 0xff000000;
		}
		short array[] = thickness.getH();
		int h;
		if (array.length <= s200)
		{
			for (i = 0; i < array.length; i++)
			{
				h = (int) ((30 - array[i] / 1092) * scale);
				for (j = h; j < s60; j++)
					imageInt[i + j * s200] = 0xffff0000;
			}
		} else
		{	// !!!
			float f=(float)array.length/s200;
			float iFloat=0;
			for (i = 0; i < s200; i++, iFloat+=f)
			{
				h = (int) ((30 - array[(int)iFloat] / 1092) * scale);
				for (j = h; j < s60; j++)
					imageInt[i + j * s200] = 0xffff0000;
				//g.drawLine(xp200mis, yp60s, xp200mis, (int) ((y + h) * scale));
			}
		}
		for (i = 0; i < s200; i++)
		{
			imageInt[i + s200 * s15] |= 0xff0000ff;
			imageInt[i + s200 * s30] |= 0xff0000ff;
			imageInt[i + s200 * s45] |= 0xff0000ff;
		}
		Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(s200, s60, imageInt, 0, s200));
		g.drawImage(image, xs, (int) (y * scale), null);
		g.drawString("+5%", xs, yp15s - 1);
		g.drawString("0%", xs + 3, yp30s - 1);
		g.drawString("-5%", xs, yp45s - 1);

	}

	// моталка
	private void drawWind(Graphics g, int x, int y, float scale, Wind wind)
	{
		int xs = (int) (x * scale);
		int ys = (int) (y * scale);
		int s20 = (int) (20 * scale);
		g.setColor(Color.BLACK);
		// размер
		//g.drawRect(xs, ys, s20, s20);
		if (wind.sis)
			g.fillOval(xs, ys, s20, s20);
		else
			g.drawOval(xs, ys, s20, s20);
		/*int d=30;
		int h=0,w=d/2;
		int h2,w2;
		for (int i=1; i<d; i++)
		{
			w2=(int)((d-i/2)/2*Math.cos(360/d*i));
			h2=(int)((d-i/2)/2*Math.sin(360/d*i));
			g.drawLine(x+d/2+w,y+d/2+h,x+d/2+w2,y+d/2+h2);
			w=w2;
			h=h2;
		}
		*/
		/*
		g.drawOval(0,0,60,60);
		g.drawArc(3,0,53,60,180,90);
		g.drawArc(3,7,53,46,90,90);
		g.drawArc(10,7,39,46,0,90);
		g.drawArc(10,14,39,32,270,90);
		*/
	}

	// устроуство не охлаждается
	private void drawNotCool(Graphics g, int x, int y, float scale)
	{
		int xs = (int) (x * scale);
		int ys = (int) (y * scale);
		// размер
		//g.drawRect(xs, ys, 3, 7);
		g.setColor(Color.RED);
		g.drawLine(xs + 1, ys, xs + 1, ys + 2);
		g.drawLine(xs, ys + 2, xs, ys + 6);
		g.drawLine(xs + 2, ys + 3, xs + 2, ys + 6);
		g.drawLine(xs + 1, ys + 7, xs + 1, ys + 7);
	}

	// устроуство не работает
	private void drawNotWork(Graphics g, int x, int y, float scale)
	{
		int s8 = (int) (8 * scale);
		int xs = (int) (x * scale);
		int ys = (int) (y * scale);
		int xp4s = (int) ((x + 4) * scale);
		g.setColor(Color.RED);
		// размер
		//g.drawRect(xs, ys, s8, s8);
		g.drawArc(xs, ys, s8, s8, 115, 310);
		g.drawLine(xp4s, ys, xp4s, (int) ((y + 4) * scale));
	}

	// клеть открыта
	private void drawOpen(Graphics g, int x, int y, float scale)
	{
		int xs = (int) (x * scale);
		int ys = (int) (y * scale);
		int s6 = (int) (6 * scale);
		// размер
		//g.drawRect(xs, ys, s6, (int) (11 * scale));
		g.drawArc(xs, ys, s6, s6, -20, 180);
		g.drawRect(xs, (int) ((y + 5) * scale), s6, s6);
	}

	private void drawI(Graphics g, int x, int y, float scale, Motor motor)
	{
		for (int i = 0; i < arrayCosI.length; i++)
			g.drawRect((int) ((x + 29 + arrayCosI[i]) * scale), (int) ((y + 19 + arraySinI[i]) * scale), 2, 2);
		float f = (float) (motor.I - Motor.I_MIN) / (Motor.I_MAX - Motor.I_MIN);
		f = (f < 0) ? -f : (f > 1.2f) ? 1.2f : f;
		double tDouble = Math.toRadians(180 * f - 180);
		double cos = Math.cos(tDouble), sin = Math.sin(tDouble);
		g.drawLine((int) ((x + 30) * scale), (int) ((y + 20) * scale), (int) ((x + 30 + 23 * cos) * scale), (int) ((y + 20 + 16 * sin) * scale));
		g.drawString(String.valueOf(motor.I), (int) ((x + 15) * scale), (int) ((y + 35) * scale));
	}

	private void drawSpeed(Graphics g, int x, int y, float scale, Motor motor)
	{
		int xp30s = (int) ((x + 30) * scale);
		int yp30s = (int) ((y + 30) * scale);
		for (int i = 0; i < arrayCos.length; i++)
			g.drawRect((int) ((x + 29 + arrayCos[i]) * scale), (int) ((y + 29 - arraySin[i]) * scale), 2, 2);
		double alRadian = Math.toRadians(225 - 270 * (motor.ref_speed - Motor.SPEED_MIN) / (Motor.SPEED_MAX - Motor.SPEED_MIN));
		g.setColor(Color.GREEN);
		g.drawLine(xp30s, yp30s, xp30s + (int) (25 * Math.cos(alRadian) * scale), yp30s - (int) (25 * Math.sin(alRadian) * scale));
		g.setColor(Color.BLACK);
		alRadian = Math.toRadians(225 - 270 * (motor.speed - Motor.SPEED_MIN) / (Motor.SPEED_MAX - Motor.SPEED_MIN));
		g.drawLine(xp30s, yp30s, xp30s + (int) (25 * Math.cos(alRadian) * scale), yp30s - (int) (25 * Math.sin(alRadian) * scale));
		g.drawString(String.valueOf(motor.speed / 10), (int) ((x + 20) * scale), (int) ((y + 55) * scale));
	}

	private void drawBits(Graphics g, int x, int y, short value)
	{
		for (int i = 0; i < 16; i++)
			if ((value & 1 << i) != 0)
				g.fillRect(x + 4 * i, y, 2, 5);
			else
				g.drawRect(x + 4 * i, y, 2, 5);
	}


	// события окна
	public void componentResized(ComponentEvent e)
	{
		// размер окна
		Component component = e.getComponent();
		if (component == null)
		{
			oldW = 787;
			oldH = 545;
		} else
		{
			int width = component.getWidth();
			int height = component.getHeight();
			if (oldW != width || oldH != height)
			{	// размер окна изменился
				float mW = 787f / width;
				float mH = 545f / height;
				if (mW < mH)
				{	// по высоте
					oldH = height;
					oldW = (int) (787 / mH);
					scale = oldH / 545f;
				} else
				{	// по ширене
					oldH = (int) (545 / mW);
					oldW = width;
					scale = oldW / 787f;
				}

				repaint();
			}
		}
	}

	public void componentMoved(ComponentEvent e)
	{
	}

	public void componentShown(ComponentEvent e)
	{
	}

	public void componentHidden(ComponentEvent e)
	{
	}
}
