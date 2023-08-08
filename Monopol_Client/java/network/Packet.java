package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import windows.Login;
import client.Client;

public abstract class Packet extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;
	private ObjectOutputStream _writer;
	private Client _client;
	private Socket _connection;

	protected Packet(Client client, Socket connection)
	{
		try
		{
			_client = client;
			_connection = connection;
			_writer = new ObjectOutputStream(connection.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public int readI(String key)
	{
		return (Integer) get(key);
	}

	public boolean readB(String key)
	{
		return (Boolean) get(key);
	}

	public String readS(String key)
	{
		return (String) get(key);
	}

	public Object read(String key)
	{
		return get(key);
	}

	protected void write(String key, Object value)
	{
		put(key, value);
	}

	private Object[][] toObjectArray()
	{
		final Object[][] objects = new Object[size()][2];
		int i = 0;
		for (Map.Entry<String, Object> entry : entrySet())
		{
			objects[i][0] = entry.getKey();
			objects[i][1] = entry.getValue();
			i++;
		}
		return objects;
	}

	protected void writeP()
	{
		try
		{
			_writer.writeObject(toObjectArray());
			_writer.flush();
		}
		catch (IOException e)
		{
			try
			{
				_connection.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "השרת נסגר, איזה באסה!", "החיבור אבד", JOptionPane.ERROR_MESSAGE);
			_client.setWindow(new Login());
		}
	}

	protected void putObjects(Object[][] objects)
	{
		clear();
		for (Object[] obj : objects)
			put(obj[0].toString(), obj[1]);
	}

	protected Socket getConnection()
	{
		return _connection;
	}

	protected Client getClient()
	{
		return _client;
	}
}