package packets;

import objects.Packet;

public abstract class PacketInterface
{
	private final Packet _packet;

	protected PacketInterface(Packet packet)
	{
		_packet = packet;

		readPacket();
		writePacket();
	}

	protected Packet getPacket()
	{
		return _packet;
	}

	protected abstract void writePacket();
	protected abstract void readPacket();

	protected int readI(String key)
	{
		return (Integer) _packet.get(key);
	}

	protected boolean readB(String key)
	{
		return (Boolean) _packet.get(key);
	}

	protected String readS(String key)
	{
		return (String) _packet.get(key);
	}

	protected Object read(String key)
	{
		return _packet.get(key);
	}

	protected void write(String key, Object value)
	{
		_packet.put(key, value);
	}
}