package packets;

import java.text.SimpleDateFormat;
import java.util.Date;

import objects.Packet;
import server.ServerThread;

public class Chat extends PacketInterface
{
	private String _username;
	private String _message;

	public Chat(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		_username = readS("sender");
		_message = readS("message");
	}

	@Override
	protected void writePacket()
	{
		if (_message != null)
			ServerThread.MESSAGES.addFirst(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " " + _username + ": " + _message);

		String messages = "";
		int i = 0;
		for (String s : ServerThread.MESSAGES)
		{
			messages = s + "\r\n" + messages;
			if (++i == 10)
				break;
		}

		write("messages", messages);
		getPacket().broadcast();
	}
}