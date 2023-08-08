package packets;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import data.AreasTable;
import objects.Packet;
import objects.User;
import objects.cards.Area;
import server.ServerThread;

public class ThrowCubes extends PacketInterface
{
	private User _thrower;
	private int _cube1;
	private int _cube2;

	public ThrowCubes(Packet packet)
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
		_thrower = getPacket().getThread().getUser();
		_thrower.setTurn(false);

		if (_thrower.getMoney() < 0)
		{
			write("packetName", "Warn");
			write("warn", true);
			write("turns", _thrower.getUnderWarning());

			_thrower.setUnderWarning(_thrower.getUnderWarning() + 1);
			_thrower.sendPacket(getPacket());
		}
		else if (_thrower.getUnderWarning() > 0)
		{
			write("packetName", "Warn");
			write("warn", false);
			write("turns", 0);

			_thrower.setUnderWarning(0);
			_thrower.sendPacket(getPacket());
		}

		write("packetName", "ThrowCubes");
		write("value", 0);
		getPacket().broadcast();

		new ScheduledThreadPoolExecutor(1).schedule(new ThrowEnd(), 2, TimeUnit.SECONDS);
	}

	private class ThrowEnd implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				_cube1 = new Random().nextInt(6) + 1;
				_cube2 = new Random().nextInt(6) + 1;

				write("packetName", "ThrowCubes");
				write("value", 1);
				write("cube1", _cube1);
				write("cube2", _cube2);
				getPacket().broadcast();

				if (_thrower.getJailValue() > 0)
				{
					if (_cube1 == _cube2)
						_thrower.setJailValue(0);
					else
						_thrower.setJailValue(_thrower.getJailValue() - 1);
	
					new ScheduledThreadPoolExecutor(1).schedule(new JailOut(), 3, TimeUnit.SECONDS);
					return;
				}

				boolean canMove = true;
				if (_cube1 == _cube2)
				{
					_thrower.setAnotherTurn(true);
					_thrower.setDoubles(_thrower.getDoubles() + 1);
					if (_thrower.getDoubles() == 3)
					{
						_thrower.setIndex(30);
	
						if (_thrower.getCardAmount(63) > 0)
							_thrower.reduceCard(63);
						else
							_thrower.setJailValue(3);
	
						canMove = false;
					}
				}
				else
					_thrower.setDoubles(0);
				if (canMove)
				{
					_thrower.setIndex(_thrower.getIndex() + _cube1 + _cube2);
					if (_thrower.getIndex() > 39)
					{
						_thrower.setDidRound();
						_thrower.setIndex(_thrower.getIndex() - 40);
						if (_thrower.getIndex() == 0)
							_thrower.incMoney(400);
						else
							_thrower.incMoney(200);
					}
				}

				getPacket().broadcastBoard();
				getPacket().broadcastTable();

				new ScheduledThreadPoolExecutor(1).schedule(new ShowResults(), 3, TimeUnit.SECONDS);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class ShowResults implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				int cardId = 0;
				int price = 0;
				boolean buyable = _thrower.didRound();
				boolean upgrade = false;
				switch (_thrower.getIndex())
				{
					// Surprises
					case 3:
					case 14:
					case 38:
						cardId = getSurpriseCard();
						break;
					// Commands
					case 6:
					case 27:
						cardId = getCommandCard();
						break;
					// Jail
					case 10:
						_thrower.setIndex(30);

						if (_thrower.getCardAmount(63) > 0)
							_thrower.reduceCard(63);
						else
							_thrower.setJailValue(3);
						break;
					// Stop
					case 20:
						_thrower.setJailValue(1);
						break;
					// Nothing
					case 0:
					case 30:
						break;
					// Others
					default:
						cardId = _thrower.getIndex();
						price = AreasTable.getInstance().get(cardId).getPrice();

						for (User user : ServerThread.getUsers())
						{
							if (user.hasArea(cardId))
							{
								if (user == _thrower)
								{
									if (user.getAreas().get(cardId) == 5 || !AreasTable.getInstance().get(cardId).isArea())
										buyable = false;
									else
									{
										price = user.getAreas().get(cardId) == 4 ? ((Area) AreasTable.getInstance().get(cardId)).getPriceHotel() : ((Area) AreasTable.getInstance().get(cardId)).getPriceHouse();
										upgrade = true;
										for (int cId : ((Area) AreasTable.getInstance().get(cardId)).getRelatedIds())
										{
											if (!user.hasArea(cId))
											{
												upgrade = false;
												buyable = false;
												break;
											}
										}
									}
									break;
								}
								else
									buyable = false;

								if (AreasTable.getInstance().get(cardId).isArea())
								{
									_thrower.decMoney(((Area) AreasTable.getInstance().get(cardId)).getCosts()[user.getAreas().get(cardId)]);
									user.incMoney(((Area) AreasTable.getInstance().get(cardId)).getCosts()[user.getAreas().get(cardId)]);
								}
								else if (AreasTable.getInstance().get(cardId).isBus())
								{
									if (_thrower.getCardAmount(54) > 0)
										_thrower.reduceCard(54);
									else
									{
										_thrower.decMoney(user.getBusPrice());
										user.incMoney(user.getBusPrice());
									}
								}
								else if (AreasTable.getInstance().get(cardId).isBusiness())
								{
									_thrower.decMoney(user.getCompanyCount() * 5 * (_cube1 + _cube2));
									user.incMoney(user.getCompanyCount() * 5 * (_cube1 + _cube2));
								}
								break;
							}
						}
						break;
				}

				write("packetName", "ThrowCubes");
				write("value", 2);
				write("card", cardId);
				write("price", price);
				write("buyable", buyable);
				write("upgrade", upgrade);
				write("money", _thrower.getMoney());
				write("name", _thrower.getName());

				getPacket().broadcast();
				getPacket().broadcastBoard();
				getPacket().broadcastTable();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private int getSurpriseCard()
	{
		int rnd = new Random().nextInt(16) + 50;
		switch (rnd)
		{
			case 50:
				_thrower.incMoney(100);
				break;
			case 51:
				new ScheduledThreadPoolExecutor(1).schedule(new Exchange(true), 3, TimeUnit.SECONDS);
				break;
			case 52:
				switch (_thrower.getIndex())
				{
					case 3:
						_thrower.setIndex(5);
						break;
					case 14:
						_thrower.setIndex(15);
						break;
					case 38:
						_thrower.setIndex(35);
						break;
				}
				break;
			case 53:
				_thrower.incMoney(30);
				break;
			case 54:
				_thrower.addCard(54);
				break;
			case 55:
				_thrower.incMoney(100);
				break;
			case 56:
				_thrower.incMoney(30 * (ServerThread.getUsers().size() - 1));
				for (User user : ServerThread.getUsers())
					if (user != _thrower)
						user.decMoney(30);
				break;
			case 57:
				_thrower.incMoney(35);
				break;
			case 58:
				_thrower.incMoney(45);
				break;
			case 59:
				_thrower.incMoney(250);
				_thrower.setIndex(0);
				_thrower.setDidRound();
				break;
			case 60:
				_thrower.incMoney(300);
				break;
			case 61:
				_thrower.incMoney(50);
				break;
			case 62:
				_thrower.incMoney(120);
				break;
			case 63:
				_thrower.addCard(63);
				break;
			case 64:
				_thrower.incMoney(300);
				break;
			case 65:
				_thrower.incMoney(75);
				break;
		}

		return rnd;
	}

	private int getCommandCard()
	{
		final int rnd = new Random().nextInt(24) + 80;
		switch (rnd)
		{
			case 80:
				_thrower.decMoney(30);
				break;
			case 81:
				boolean found = false;
				for (User user : ServerThread.getUsers())
				{
					if (user.hasArea(17))
					{
						found = true;

						if (user == _thrower)
							break;

						_thrower.decMoney((_cube1 + _cube2) * 10);
						user.incMoney((_cube1 + _cube2) * 10);
						break;
					}
				}
				if (!found)
					_thrower.decMoney(20);
				break;
			case 82:
				_thrower.decMoney(_thrower.getHouseCount() * 10);
				_thrower.decMoney(_thrower.getHotelCount() * 20);
				break;
			case 83:
				found = false;
				for (User user : ServerThread.getUsers())
				{
					if (user.hasArea(8))
					{
						found = true;

						if (user == _thrower)
							break;

						_thrower.decMoney((_cube1 + _cube2) * 10);
						user.incMoney((_cube1 + _cube2) * 10);
						break;
					}
				}
				if (!found)
					_thrower.decMoney(40);
				break;
			case 84:
				_thrower.decMoney(250);
				break;
			case 85:
				_thrower.setIndex(30);

				if (_thrower.getCardAmount(63) > 0)
					_thrower.reduceCard(63);
				else
					_thrower.setJailValue(3);
				break;
			case 86:
				_thrower.decMoney(30);
				break;
			case 87:
				_thrower.decMoney(40);
				break;
			case 88:
				_thrower.decMoney(45);
				break;
			case 89:
				_thrower.decMoney(190);
				break;
			case 90:
				_thrower.decMoney(50);
				break;
			case 91:
				_thrower.decMoney(50);
				break;
			case 92:
				_thrower.decMoney(175);
				break;
			case 93:
				new ScheduledThreadPoolExecutor(1).schedule(new Exchange(false), 3, TimeUnit.SECONDS);
				break;
			case 94:
				_thrower.decMoney(30);
				break;
			case 95:
				_thrower.decMoney(150);
				break;
			case 96:
				_thrower.decMoney(25);
				break;
			case 97:
				switch (_thrower.getIndex())
				{
					case 6:
						_thrower.setIndex(5);
						break;
					case 27:
						_thrower.setIndex(25);
						break;
				}
				for (User user : ServerThread.getUsers())
				{
					if (user.hasArea(_thrower.getIndex()))
					{
						if (user == _thrower)
							break;

						if (_thrower.getCardAmount(54) > 0)
						{
							_thrower.reduceCard(54);
							_thrower.decMoney(5);
							user.incMoney(5);
						}
						else
						{
							_thrower.decMoney(user.getBusPrice());
							user.incMoney(user.getBusPrice());
						}
						break;
					}
				}
				break;
			case 98:
				_thrower.decMoney(20);
				break;
			case 99:
				_thrower.decMoney(75);
				break;
			case 100:
				_thrower.decMoney(25);
				break;
			case 101:
				_thrower.decMoney(25);
				break;
			case 102:
				_thrower.decMoney(35);
				break;
			case 103:
				_thrower.decMoney(_thrower.getHouseCount() * 15);
				break;
		}

		return rnd;
	}

	private class Exchange implements Runnable
	{
		private boolean _isCommand;

		private Exchange(boolean isCommand)
		{
			_isCommand = isCommand;
		}

		@Override
		public void run()
		{
			try
			{
				write("packetName", "ThrowCubes");
				write("value", 2);
				write("card", _isCommand ? getCommandCard() : getSurpriseCard());
				write("price", 0);
				write("buyable", false);
				write("upgrade", false);
				write("money", _thrower.getMoney());
				write("name", _thrower.getName());

				getPacket().broadcast();
				getPacket().broadcastBoard();
				getPacket().broadcastTable();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class JailOut implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				getPacket().setObjects(new Object[][] {{"packetName", "Purchase"}, {"card", -1}});
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}