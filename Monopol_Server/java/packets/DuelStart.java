package packets;

import java.awt.Color;
import java.util.Random;

import objects.Packet;
import objects.User;
import server.ServerThread;

public class DuelStart extends PacketInterface
{
	private static final Color[] COLORS = 
	{
		Color.RED,
		Color.CYAN,
		Color.LIGHT_GRAY,
		Color.GREEN,
		Color.ORANGE,
		Color.PINK,
		Color.WHITE,
		Color.YELLOW
	};

	public DuelStart(Packet packet)
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
		ServerThread.GAME_STARTED = true;

		int i = 0;
		final int rnd = new Random().nextInt(ServerThread.getUsers().size());
		ServerThread.setI(rnd);
		for (User user : ServerThread.getUsers())
		{
			user.setMoney(1830);
			user.setColor(COLORS[i]);
			user.setTurn(rnd == i);
			user.setIndex(0);

			i++;
		}

		getPacket().broadcast();
		getPacket().broadcastBoard();
		getPacket().broadcastTable();
	}
}