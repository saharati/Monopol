package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import objects.Bid;
import objects.Packet;
import objects.User;

public class ServerThread extends Thread
{
	public static final LinkedList<String> MESSAGES = new LinkedList<String>();
	public static boolean GAME_STARTED = false;
	public static Bid BID;

	private static final List<User> _users = new CopyOnWriteArrayList<User>();
	private static int _i = 0;

	private final Socket _connection;
	private final Packet _packet;
	private ObjectOutputStream _writer;
	private User _user;

	public ServerThread(Socket connection)
	{
		_connection = connection;
		_packet = new Packet(this);

		try
		{
			_writer = new ObjectOutputStream(connection.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static List<User> getUsers()
	{
		return _users;
	}

	public static User getUserByName(String name)
	{
		for (User user : _users)
			if (name.equals(user.getName()))
				return user;

		return null;
	}

	public static User getNextUser()
	{
		_i++;
		if (_i >= _users.size())
			_i = 0;

		return _users.get(_i);
	}

	public static void setI(int i)
	{
		_i = i;
	}

	public Socket getConnection()
	{
		return _connection;
	}

	public Packet getPacket()
	{
		return _packet;
	}

	public ObjectOutputStream getWriter()
	{
		return _writer;
	}

	public User getUser()
	{
		return _user;
	}

	public void setUser(User user)
	{
		_user = user;

		_users.add(user);
	}

	@Override
	public void run()
	{
		try
		{
			final ObjectInputStream reader = new ObjectInputStream(_connection.getInputStream());
			while (true)
			{
				synchronized (_packet)
				{
					_packet.setObjects((Object[][]) reader.readObject());
					_packet.clear();
				}
			}
		}
		catch (IOException e)
		{
			try
			{
				if (!_connection.isClosed())
					_connection.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}

			if (_user != null)
			{
				_users.remove(_user);
				if (GAME_STARTED)
				{
					if (_users.isEmpty())
					{
						GAME_STARTED = false;
						BID = null;
					}
					else
					{
						if (_user.getTurn())
						{
							User nextUser;
							while (true)
							{
								nextUser = ServerThread.getNextUser();
								if (nextUser.getJailValue() > 0 && nextUser.getIndex() == 20)
									nextUser.setJailValue(nextUser.getJailValue() - 1);
								else
									break;
							}

							nextUser.setTurn(true);
						}
						_packet.broadcastBoard();
						_packet.broadcastTable();
					}
				}
				else
					_packet.setObjects(new Object[][] {{"packetName", "WaitingRoom"}});
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}