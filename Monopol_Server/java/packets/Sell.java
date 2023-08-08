package packets;

import objects.Packet;

public class Sell extends PacketInterface
{
	private int _areaId;

	public Sell(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		_areaId = readI("index");
	}

	@Override
	protected void writePacket()
	{
		write("valid", getPacket().getThread().getUser().hasArea(_areaId));

		getPacket().getThread().getUser().sendPacket(getPacket());
	}
}