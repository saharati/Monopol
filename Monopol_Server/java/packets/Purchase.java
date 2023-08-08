package packets;

import data.AreasTable;
import objects.Packet;
import objects.User;
import objects.cards.Area;
import server.ServerThread;

public class Purchase extends PacketInterface
{
	private int _cardId;

	public Purchase(Packet packet)
	{
		super(packet);
	}

	@Override
	protected void readPacket()
	{
		_cardId = readI("card");
	}

	@Override
	protected void writePacket()
	{
		final User user = getPacket().getThread().getUser();
		if (_cardId > 0)
		{
			if (user.hasArea(_cardId) && AreasTable.getInstance().get(_cardId).isArea())
			{
				final Area area = (Area) AreasTable.getInstance().get(_cardId);
				final int price = user.getAreas().get(_cardId) == 4 ? area.getPriceHotel() : area.getPriceHouse();
				if (user.getMoney() >= price)
				{
					user.decMoney(price);
					user.addHouse(_cardId);
				}
			}
			else
			{
				final int price = AreasTable.getInstance().get(_cardId).getPrice();
				if (user.getMoney() >= price)
				{
					user.decMoney(price);
					user.addArea(_cardId);
				}
			}
		}

		boolean nextPlayer = true;
		if (user.hasAnotherTurn())
		{
			user.setAnotherTurn(false);

			if (user.getJailValue() > 0)
				user.setJailValue(user.getJailValue() - 1);
			else
			{
				user.setTurn(true);
				nextPlayer = false;
			}
		}
		if (nextPlayer)
		{
			User nextUser;
			while (true)
			{
				nextUser = ServerThread.getNextUser();
				if (nextUser.getJailValue() > 0 && nextUser.getIndex() == 20)
					nextUser.setJailValue(nextUser.getJailValue() - 1);
				else
					break;
			}

			nextUser.setTurn(true);
		}

		getPacket().broadcastBoard();
		getPacket().broadcastTable();
	}
}