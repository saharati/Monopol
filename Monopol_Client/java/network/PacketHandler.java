package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import client.Client;

public class PacketHandler extends Packet
{
	private static final long serialVersionUID = 1L;
	private ObjectInputStream _reader;

	public PacketHandler(Client client, Socket connection)
	{
		super(client, connection);

		try
		{
			_reader = new ObjectInputStream(connection.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void login(String username)
	{
		write("packetName", "Login");
		write("username", username);
		writeP();
	}

	public void refreshWaitingRoom()
	{
		write("packetName", "WaitingRoom");
		writeP();
	}

	public void sendMessage(String sender, String message)
	{
		write("packetName", "Chat");
		write("sender", sender);
		write("message", message);
		writeP();
	}

	public void requestBattleStart()
	{
		write("packetName", "DuelStart");
		writeP();
	}

	public void throwCubes()
	{
		write("packetName", "ThrowCubes");
		writeP();
	}

	public void pay()
	{
		write("packetName", "Pay");
		writeP();
	}

	public void sell(int index)
	{
		write("packetName", "Sell");
		write("index", index);
		writeP();
	}

	public void requestSellTrade(String command, int wantedMoney, Integer[] wantedAreas)
	{
		write("packetName", "SellTrade");
		write("command", command);
		write("wantedMoney", wantedMoney);
		write("wantedAreas", wantedAreas);
		writeP();
	}

	public void purchase(int cardId)
	{
		write("packetName", "Purchase");
		write("card", cardId);
		writeP();
	}

	public void sendBid(int money)
	{
		write("packetName", "SendBid");
		write("money", money);
		writeP();
	}

	public void cancelBid()
	{
		write("packetName", "CancelBid");
		writeP();
	}

	public void onBid(int count)
	{
		write("packetName", "OnBid");
		write("timer", count);
		writeP();
	}

	public Packet getResponse()
	{
		try
		{
			synchronized (this)
			{
				final Object obj = _reader.readObject();
				if (obj instanceof Object[][])
					putObjects((Object[][]) obj);
				return this;
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// Disconnected
		}

		return null;
	}
}