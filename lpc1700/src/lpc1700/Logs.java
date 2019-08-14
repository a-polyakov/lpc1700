package lpc1700;

import lpc1700.okna.PanelLog;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 11.12.2007
 * Time: 10:19:36
 * Формирование сообщений
 */
public class Logs
{
	public static PanelLog panLog;

	public static void write(String mes)
	{
		if (panLog != null)
			panLog.write(mes);
	}
}
