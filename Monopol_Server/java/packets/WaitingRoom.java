package packets;

import objects.Packet;
import objects.User;
import server.ServerThread;

public class WaitingRoom extends PacketInterface
{
	public WaitingRoom(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		
	}

	@Override
	protected void writePacket()
	{
		final String[][] users = new String[ServerThread.getUsers().size()][1];
		int i = 0;
		for (User user : ServerThread.getUsers())
			users[i++][0] = user.getName();

		write("users", users);
		getPacket().broadcast();
	}
}