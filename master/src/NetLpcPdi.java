import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 12.09.2007
 * Time: 8:45:35
 * При запуске процесса поддерживает соединение и ждет данных для отправления
 */
public class NetLpcPdi extends Thread
{
	private Index index;

	private Socket socket;
	private boolean stop;
	private boolean send;
	private String socketIp;
	private int socketPort;
	private Party party;

	public NetLpcPdi(Index index, String ip, int port)
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
				DataOutputStream dout=new DataOutputStream(out);
				index.netEventConnect(this);
				setPriority(5);
				while (!stop)
				{
					// ждем разрешения на отправку данных
					setPriority(1);
					while (!stop && !send)
						sleep(80);
					setPriority(5);
					if (!stop && party != null)
					{	// передача данные
						dout.writeUTF(party.toString());
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

	public void send(Party party)
	{
		this.party = party;
		send=true;
	}

	public void close()
	{
		stop = true;
	}
}
