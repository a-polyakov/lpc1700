import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.*;
import java.util.Arrays;
import java.util.Vector;
import java.util.zip.*;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 12.09.2007
 * Time: 8:44:50
 * //Track ????
 *
 */
public class NetPC extends Thread
{
	static int PORT_PC=1124;
	private DatagramSocket pcDatagram;
	private DatagramPacket packOut;
	private DatagramPacket packIn;
	private Index index;

	private byte byteQuery[];
	private byte bytePC[];
	private byte byteMaster[];
	private byte bytePdi[];
	private boolean stop;
	private boolean send;

	private Vector vector;
	private int indexVector;

	private DeflaterOutputStream outZip;
	//private GZIPOutputStream outZip;
	private InflaterInputStream inZip;
	//private GZIPInputStream inZip;

	public NetPC(Index index)
	{
		this.index = index;
		byteQuery = "MASTER".getBytes();
		bytePC = new byte[Index.LENGTH_MASTER +6+7*Index.LENGTH_PDI];
		stop = false;
		send = false;
		vector=new Vector();
		indexVector=0;

		try
		{
			OutputStream outFile=new FileOutputStream("dat.zip");
			outZip=new DeflaterOutputStream(outFile);
		}
		catch(FileNotFoundException e)
		{
			index.writeLog(e.getMessage());
		}
		catch(IOException e)
		{
			index.writeLog(e.getMessage());
		}
		inZip=null;
	}

	public void run()
	{
		while (!stop)
			try
			{
				pcDatagram = new DatagramSocket();
				pcDatagram.setSoTimeout(80);
				packOut = new DatagramPacket(byteQuery, byteQuery.length, InetAddress.getByName("192.168.0.2"), PORT_PC);
				packIn = new DatagramPacket(bytePC, bytePC.length);
				while (!stop)
				{
					if (index.sendAuto.isSelected())
					{
						sleep(80);
					} else
					{
						setPriority(1);
						while (!stop && !send)
							sleep(80);
						setPriority(5);
					}
					if (!stop)
					{	// передача данные
						if (index.fileLoad.isSelected())
						{
							for (int i=0; i<bytePC.length; i++)
								bytePC[i]=(byte)inZip.read();
						}
						else
						{
							pcDatagram.send(packOut);
							pcDatagram.receive(packIn);
						}
						if (outZip!=null && index.fileSave.isSelected())
						{
							outZip.write(bytePC);
						}
						byteMaster = Arrays.copyOf(bytePC, Index.LENGTH_MASTER); // Mas-Vax
						// Arrays.copyOfRange(bytePC, Index.LENGTH_MASTER, Index.LENGTH_MASTER+6); //Track
						bytePdi = Arrays.copyOfRange(bytePC, Index.LENGTH_MASTER+6, Index.LENGTH_MASTER + 6 + 7*Index.LENGTH_PDI); // PDI[7]
						index.netMasterLpc.send(byteMaster);
						if (index.netPdiLpc != null)
						{	// связь с сервером запущена
							Party.readPdi(bytePdi, vector);
							if (vector.size()>indexVector)
							{
								index.netPdiLpc.send((Party)vector.elementAt(indexVector));
								indexVector++;
							}
						}
						send = false;
					}
				}
				if (outZip!=null)
					outZip.close();
				if (inZip!=null)
					inZip.close();
				pcDatagram.close();
				index.writeLog("соединение с PC разорвано");
			}
			catch (IOException e)
			{
				index.writeLog("Ошибка при соединении с PC: " + e.getMessage());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}

	public void send()
	{
		send = true;
	}

	public void close()
	{
		stop = true;
	}

	public void setInFile(String fileName)
	{
		try
		{
			InputStream inFile=new FileInputStream(fileName);
			inZip=new InflaterInputStream(inFile);
		}
		catch(FileNotFoundException e)
		{
			inZip=null;
			index.writeLog(e+e.getMessage());
		}
		catch(IOException e)
		{
			index.writeLog(e.getMessage());
		}
	}
}

