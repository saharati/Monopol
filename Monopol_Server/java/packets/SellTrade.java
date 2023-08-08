package packets;

import java.util.ArrayList;
import java.util.List;

import data.AreasTable;
import objects.Bid;
import objects.Packet;
import objects.User;
import objects.cards.Area;
import objects.cards.MasterCard;
import server.ServerThread;

public class SellTrade extends PacketInterface
{
	private String _command;
	private int _wantedMoney;
	private Integer[] _wantedAreas;

	public SellTrade(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		_command = readS("command");
		_wantedMoney = readI("wantedMoney");
		_wantedAreas = (Integer[]) read("wantedAreas");
	}

	@Override
	protected void writePacket()
	{
		final ServerThread thread = getPacket().getThread();
		final User user = thread.getUser();
		final List<Integer> areasToGive = new ArrayList<Integer>();
		final List<Integer> areasToGet = new ArrayList<Integer>();
		for (int area : _wantedAreas)
		{
			if (areasToGive.contains(area) || areasToGet.contains(area))
				continue;

			if (user.hasArea(area))
				areasToGive.add(area);
			else
				areasToGet.add(area);
		}
		if (_command.equals("Bank"))
		{
			if (ServerThread.BID != null)
				return;

			for (int a : areasToGive)
			{
				final MasterCard card = AreasTable.getInstance().get(a);
				final int houses = user.getAreas().get(a);
				int retMoney = 0;
				if (card.isArea())
				{
					final Area area = (Area) card;
					if (houses == 5)
						retMoney = area.getPriceHotel() / 2;
					else if (houses > 0)
						retMoney = area.getPriceHouse() / 2;
					else
						retMoney = area.getPrice() / 2;
					retMoney = Math.round(retMoney);
				}
				user.incMoney(Math.round(retMoney / 5) * 5);
				if (houses > 0)
					user.getAreas().put(a, houses - 1);
				else
					user.getAreas().remove(a);
			}
		}
		else if (_command.equals("Players"))
		{
			if (ServerThread.BID != null)
			{
				write("valid", -1);

				user.sendPacket(getPacket());
			}
			else
			{
				boolean hasAllAreas = true;
				if (!areasToGet.isEmpty())
				{
					for (User u : ServerThread.getUsers())
					{
						if (u == user)
							continue;

						if (u.hasArea(areasToGet.get(0)))
						{
							for (int area : areasToGet)
							{
								if (!u.hasArea(area))
								{
									hasAllAreas = false;
									break;
								}
							}
						}
					}
				}
				if (hasAllAreas && !areasToGive.isEmpty())
				{
					for (User u : ServerThread.getUsers())
					{
						if (u != user)
							continue;

						if (u.hasArea(areasToGive.get(0)))
						{
							for (int area : areasToGive)
							{
								if (!u.hasArea(area))
								{
									hasAllAreas = false;
									break;
								}
							}
						}
					}
				}
				if (!hasAllAreas)
				{
					write("valid", -2);

					user.sendPacket(getPacket());
				}
				else
				{
					ServerThread.BID = new Bid(user, _wantedMoney, areasToGive, areasToGet);

					final List<String> give = new ArrayList<String>();
					final List<String> get = new ArrayList<String>();
					for (int a : areasToGive)
						give.add(AreasTable.getInstance().get(a).getName());
					for (int a : areasToGet)
						get.add(AreasTable.getInstance().get(a).getName());

					write("valid", 0);
					write("requester", user.getName());
					write("areasToGive", give.toArray(new String[give.size()]));
					write("areasToGet", get.toArray(new String[get.size()]));

					getPacket().broadcast();
				}
			}
		}

		getPacket().broadcastBoard();
		getPacket().broadcastTable();
	}
}