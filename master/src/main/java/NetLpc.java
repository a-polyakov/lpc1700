import javax.swing.*;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 12.09.2007
 * Time: 8:45:35
 * При запуске процесса поддерживает соединение и ждет данных для отправления
 */
public class NetLpc extends Thread
{
	private Index index;

	private Socket socket;
	private boolean stop;
	private boolean send;
	private byte byfer[];
	private String socketIp;
	private int socketPort;

	public NetLpc(Index index, String ip, int port)
	{
		this.index = index;
		socketIp = ip;
		socketPort = port;
		stop = false;
		send = false;
	}

	public void run()
	{
		while (!stop)
			try
			{
				// подключиться
				socket = new Socket(socketIp, socketPort);
				OutputStream out = socket.getOutputStream();
				index.netEventConnect(this);
				setPriority(5);
				while (!stop)
				{
					// ждем разрешения на отправку данных
					setPriority(1);
					while (!stop && !send)
						sleep(80);
					setPriority(5);
					if (!stop && byfer != null)
					{	// передача данные
						out.write(byfer);
						send = false;
					}
				}
				out.close();
				socket.close();
				index.netEventClose(this);
			}
			catch (IOException e)
			{
				index.netEventError(this, e.getMessage());
				setPriority(1);
			}
			catch (InterruptedException e)
			{
			}
	}

	public void send(byte byfer[])
	{
		this.byfer = byfer;
		send = true;
	}

	public void close()
	{
		stop = true;
	}
}
