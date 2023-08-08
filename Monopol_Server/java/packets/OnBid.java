package packets;

import objects.Packet;
import server.ServerThread;

public class OnBid extends PacketInterface
{
	private int _timer;

	public OnBid(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		_timer = readI("timer");
	}

	@Override
	protected void writePacket()
	{
		if (_timer > 0)
			getPacket().broadcast();
		else if (ServerThread.BID == null) // FIXME temp fix
		{
			System.out.println("Bid is null when timer is " + _timer);

			write("timer", 0);

			getPacket().broadcast();
			getPacket().broadcastBoard();
			getPacket().broadcastTable();
		}
		else
		{
			if (!ServerThread.BID.getBidders().isEmpty())
				ServerThread.BID.exchange();

			ServerThread.BID = null;

			getPacket().broadcast();
			getPacket().broadcastBoard();
			getPacket().broadcastTable();
		}
	}
}