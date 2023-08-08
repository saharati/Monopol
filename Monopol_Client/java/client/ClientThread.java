package client;

import javax.swing.JOptionPane;

import windows.ChatRoom;
import windows.DuelRoom;
import windows.WaitingRoom;
import network.Packet;
import client.Client;

public class ClientThread extends Thread
{
	private final Client _client;

	public ClientThread(Client client)
	{
		_client = client;
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				final Packet response = _client.getConnection().getResponse();
				if (response == null)
					return;

				final String command = response.readS("packetName");
				if (command.equals("Login"))
				{
					if (response.readB("in"))
					{
						_client.setWindow(new WaitingRoom(_client));
						_client.getConnection().refreshWaitingRoom();
						_client.setChatRoom(new ChatRoom(_client));
						_client.getConnection().sendMessage(_client.getName(), null);
					}
					else
						JOptionPane.showMessageDialog(null, "איזה באסה...", "הכניסה נכשלה.", JOptionPane.ERROR_MESSAGE);
				}
				else if (command.equals("WaitingRoom"))
					((WaitingRoom) _client.getWindow()).reloadTable((String[][]) response.read("users"));
				else if (command.equals("Chat"))
					((ChatRoom) _client.getChatRoom()).getChatWindow().setText(response.readS("messages"));
				else if (command.equals("DuelStart"))
					_client.setWindow(new DuelRoom(_client));
				else if (command.equals("DuelRoom"))
					((DuelRoom) _client.getWindow()).reloadTable((Object[][]) response.read("players"));
				else if (command.equals("Board"))
					((DuelRoom) _client.getWindow()).reloadBoard((Object[][]) response.read("players"));
				else if (command.equals("ThrowCubes"))
				{
					if (response.readI("value") == 0)
						((DuelRoom) _client.getWindow()).throwCubes();
					else if (response.readI("value") == 1)
						((DuelRoom) _client.getWindow()).endCubes(response.readI("cube1"), response.readI("cube2"));
					else
						((DuelRoom) _client.getWindow()).showCard(response.readI("card"), response.readI("price"), response.readB("buyable"), response.readB("upgrade"), response.readI("money"), response.readS("name"));
				}
				else if (command.equals("Sell"))
				{
					if (response.readB("valid"))
						((DuelRoom) _client.getWindow()).showSellDialog(response.readI("index"));
				}
				else if (command.equals("SellTrade"))
				{
					switch (response.readI("valid"))
					{
					case -1:
						JOptionPane.showMessageDialog(null, "כבר קיימת הצעה על הלוח, אנא המתן לסיומה.", "הפעולה נכשלה.", JOptionPane.ERROR_MESSAGE);
						break;
					case -2:
						JOptionPane.showMessageDialog(null, "כל השטחים שאתה מבקש חייבים להיות מאותו שחקן.", "הפעולה נכשלה.", JOptionPane.ERROR_MESSAGE);
						break;
					default:
						((DuelRoom) _client.getWindow()).placeBid(response.readS("requester"), response.readI("wantedMoney"), (String[]) response.read("areasToGive"), (String[]) response.read("areasToGet"));
						break;
					}
				}
				else if (command.equals("CancelBid"))
				{
					if (response.readB("cancelled"))
						((DuelRoom) _client.getWindow()).cancelBid();
				}
				else if (command.equals("OnBid"))
					((DuelRoom) _client.getWindow()).onBid(response.readI("timer"));
				else if (command.equals("Warn"))
					((DuelRoom) _client.getWindow()).warn(response.readB("warn"), response.readI("turns"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}