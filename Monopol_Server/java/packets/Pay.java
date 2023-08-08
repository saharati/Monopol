package packets;

import objects.Packet;
import objects.User;

public class Pay extends PacketInterface
{
	public Pay(Packet packet)
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
		final User user = getPacket().getThread().getUser();
		user.setTurn(false);
		user.setJailValue(0);
		user.decMoney(100);

		getPacket().setObjects(new Object[][] {{"packetName", "ThrowCubes"}});
	}
}