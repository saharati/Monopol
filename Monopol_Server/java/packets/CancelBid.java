package packets;

import objects.Packet;
import server.ServerThread;

public class CancelBid extends PacketInterface
{
	public CancelBid(Packet packet)
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
		if (ServerThread.BID == null)
			return;

		if (ServerThread.BID.getRequester() == getPacket().getThread().getUser())
		{
			ServerThread.BID = null;
			write("cancelled", true);

			getPacket().broadcast();
		}
		else
		{
			ServerThread.BID.getBidders().remove(getPacket().getThread().getUser());
			write("cancelled", false);
		}
	}
}