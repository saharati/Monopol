package objects;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import server.ServerThread;

public class Packet extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;
	private final ServerThread _thread;

	public Packet(ServerThread thread)
	{
		_thread = thread;
	}

	public void setObjects(Object[][] objects)
	{
		for (Object[] obj : objects)
			put(obj[0].toString(), obj[1]);

		try
		{
			Class.forName("packets." + get("packetName")).getConstructors()[0].newInstance(this);
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Couldn't find target: packets." + get("packetName"));
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	public Object[] toObjectArray()
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

	public void broadcast()
	{
		for (User user : ServerThread.getUsers())
			user.sendPacket(this);

		clear();
	}

	public void broadcastTable()
	{
		final Object[][] players = new Object[ServerThread.getUsers().size()][5];
		int i = 0;
		for (User user : ServerThread.getUsers())
		{
			players[i][0] = user.getName();
			players[i][1] = user.getMoney();
			players[i][2] = user.getTurn();
			players[i][3] = user.getIndex() == 30 && user.getJailValue() > 0;
			players[i][4] = user.getColor();

			i++;
		}

		put("packetName", "DuelRoom");
		put("players", players);
		broadcast();
	}

	public void broadcastBoard()
	{
		final Object[][] players = new Object[ServerThread.getUsers().size()][3];
		int i = 0;
		for (User user : ServerThread.getUsers())
		{
			players[i][0] = user.getColor();
			players[i][1] = user.getIndex();

			final int[][] areas = new int[user.getAreas().size()][2];
			int j = 0;
			for (Entry<Integer, Integer> e : user.getAreas().entrySet())
			{
				areas[j][0] = e.getKey();
				areas[j][1] = e.getValue();

				j++;
			}

			players[i][2] = areas;

			i++;
		}

		put("packetName", "Board");
		put("players", players);
		broadcast();
	}

	public ServerThread getThread()
	{
		return _thread;
	}
}