package packets;

import java.io.IOException;

import objects.Packet;
import objects.User;
import server.ServerThread;

public class Login extends PacketInterface
{
	private String _username;

	public Login(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		_username = readS("username");
	}

	@Override
	protected void writePacket()
	{
		if (ServerThread.getUserByName(_username) != null || ServerThread.GAME_STARTED)
			write("in", false);
		else
		{
			getPacket().getThread().setUser(new User(_username, getPacket().getThread()));

			write("in", true);
		}

		try
		{
			getPacket().getThread().getWriter().writeObject(getPacket().toObjectArray());
			getPacket().getThread().getWriter().flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}