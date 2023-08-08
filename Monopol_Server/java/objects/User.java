package objects;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import objects.cards.Bus;
import objects.cards.Business;
import data.AreasTable;
import server.ServerThread;

public class User
{
	private final String _name;
	private final ServerThread _thread;
	private final Map<Integer, Integer> _areas = new ConcurrentHashMap<Integer, Integer>();
	private final Map<Integer, Integer> _cards = new ConcurrentHashMap<Integer, Integer>();
	private int _money;
	private int _index;
	private int _jail;
	private int _doubles;
	private int _turnsUnderWarning;
	private boolean _hasAnotherTurn;
	private boolean _turn;
	private boolean _didRound;
	private Color _color;

	public User(String name, ServerThread thread)
	{
		_name = name;
		_thread = thread;
	}

	public String getName()
	{
		return _name;
	}

	public ServerThread getThread()
	{
		return _thread;
	}

	public int getMoney()
	{
		return _money;
	}

	public void incMoney(int amount)
	{
		_money += amount;
	}

	public void decMoney(int amount)
	{
		_money -= amount;
	}

	public void setMoney(int money)
	{
		_money = money;
	}

	public Color getColor()
	{
		return _color;
	}

	public void setColor(Color color)
	{
		_color = color;
	}

	public boolean getTurn()
	{
		return _turn;
	}

	public void setTurn(boolean turn)
	{
		_turn = turn;
	}

	public int getIndex()
	{
		return _index;
	}

	public void setIndex(int index)
	{
		_index = index;
	}

	public Map<Integer, Integer> getAreas()
	{
		return _areas;
	}

	public boolean hasArea(int id)
	{
		return _areas.containsKey(id);
	}

	public void addArea(int id)
	{
		_areas.put(id, 0);
	}

	public int getBusCount()
	{
		int count = 0;
		for (Bus b : AreasTable.getInstance().getBuses())
			if (_areas.containsKey(b.getId()))
				count++;

		return count;
	}

	public int getBusPrice()
	{
		switch (getBusCount())
		{
			case 1:
				return 30;
			case 2:
				return 60;
			case 3:
				return 120;
			case 4:
				return 240;
		}

		return 0;
	}

	public void addHouse(int id)
	{
		_areas.put(id, _areas.get(id) + 1);
	}

	public int getHouseCount()
	{
		int count = 0;
		for (int houses : _areas.values())
			if (houses < 5)
				count += houses;

		return count;
	}

	public int getHotelCount()
	{
		int count = 0;
		for (int houses : _areas.values())
			if (houses == 5)
				count++;

		return count;
	}

	public int getCompanyCount()
	{
		int count = 0;
		for (Business b : AreasTable.getInstance().getBusinesses())
			if (_areas.containsKey(b.getId()))
				count++;

		return count;
	}

	public int getCardAmount(int id)
	{
		if (!_cards.containsKey(id))
			return 0;

		return _cards.get(id);
	}

	public void addCard(int id)
	{
		if (_cards.containsKey(id))
			_cards.put(id, _cards.get(id) + 1);
		else
			_cards.put(id, 1);
	}

	public void reduceCard(int id)
	{
		if (!_cards.containsKey(id))
			return;

		if (_cards.get(id) == 1)
			_cards.remove(id);
		else
			_cards.put(id, _cards.get(id) - 1);
	}

	public int getJailValue()
	{
		return _jail;
	}

	public void setJailValue(int jail)
	{
		_jail = jail;
	}

	public int getUnderWarning()
	{
		return _turnsUnderWarning;
	}

	public void setUnderWarning(int underWarning)
	{
		_turnsUnderWarning = underWarning;
	}

	public boolean hasAnotherTurn()
	{
		return _hasAnotherTurn;
	}

	public void setAnotherTurn(boolean anotherTurn)
	{
		_hasAnotherTurn = anotherTurn;
	}

	public boolean didRound()
	{
		return _didRound;
	}

	public void setDidRound()
	{
		_didRound = true;
	}

	public int getDoubles()
	{
		return _doubles;
	}

	public void setDoubles(int doubles)
	{
		_doubles = doubles;
	}

	public void sendPacket(Packet packet)
	{
		try
		{
			_thread.getWriter().writeObject(packet.toObjectArray());
			_thread.getWriter().flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}