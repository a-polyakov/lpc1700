package lpc1700.network;

import lpc1700.Index;
import lpc1700.stan.Party;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 11.09.2007
 * Time: 7:56:15
 * Сетевое соединение с PDI
 * <p/>
 * первый байт означает действие необходимое сделать,
 * остальное данные
 * <p/>
 * время последних данных о партии
 */
public class NetPdi extends Thread
{
	private boolean stop;
	private Index index;
	private ServerSocket serverSocket;
	private Socket socket;
	private InputStream in;

	public NetPdi(Index index)
	{
		this.index = index;

	}

	// запустить сервер
	public void run()
	{
		try
		{
			serverSocket = new ServerSocket(Index.SOCKET_PORT_PDI);
			index.netEventRun(this);
			while (!stop)
				try
				{
					// ждать подключения
					socket = serverSocket.accept();
					in = socket.getInputStream();
					index.netEventConnect(this);
					while (!stop)
						readPdi(in);	// получить данные
					in.close();
					socket.close();
					index.netEventStop(this);
				}
				catch (EOFException e)
				{
					index.netEventClose(this);
				}
				catch (IOException e)
				{
					index.netEventError(this, e.getMessage());
				}
		}
		catch (IOException e)
		{
			index.netEventError(this, "Ошибка при инициализации сервера: " + e.getMessage());
			index.netEventStop(this);
		}
	}

	// остановить сервер
	public void close()
	{
		stop = true;
		try
		{
			if (serverSocket != null)
				serverSocket.close();
		}
		catch (IOException e)
		{
		}
	}

	// !!!
	private void readPdi(InputStream in) throws IOException
	{
		DataInputStream din=new DataInputStream(in);
		String s=din.readUTF();
		if (s.substring(0,s.indexOf('\t')).equals("party"))
		{	// введена новая партия
			Party party = new Party(index.stan.posProcent.getPosProcent());
			party.fromString(s);
			index.newPartiya(party);
		}
	}

}
