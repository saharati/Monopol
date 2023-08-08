package packets;

import objects.Packet;
import server.ServerThread;

public class SendBid extends PacketInterface
{
	private int _money;

	public SendBid(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		_money = readI("money");
	}

	@Override
	protected void writePacket()
	{
		if (ServerThread.BID == null || ServerThread.BID.getRequester() == getPacket().getThread().getUser())
			return;

		if (_money >= ServerThread.BID.getMinBid() && getPacket().getThread().getUser().getMoney() >= _money && ServerThread.BID.hasAllAreas(getPacket().getThread().getUser()))
			ServerThread.BID.getBidders().put(getPacket().getThread().getUser(), _money);
		else
			ServerThread.BID.getBidders().remove(getPacket().getThread().getUser());
	}
}