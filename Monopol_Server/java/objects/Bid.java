package objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Bid
{
	private final User _requester;
	private final int _minBid;
	private final List<Integer> _areasToGive;
	private final List<Integer> _areasToGet;
	private final Map<User, Integer> _bidders = new ConcurrentHashMap<User, Integer>();

	public Bid(User requester, int minBid, List<Integer> areasToGive, List<Integer> areasToGet)
	{
		_requester = requester;
		_minBid = minBid;
		_areasToGive = areasToGive;
		_areasToGet = areasToGet;
	}

	public User getRequester()
	{
		return _requester;
	}

	public int getMinBid()
	{
		return _minBid;
	}

	public Map<User, Integer> getBidders()
	{
		return _bidders;
	}

	public List<Integer> getAreasToGive()
	{
		return _areasToGive;
	}

	public List<Integer> getAreasToGet()
	{
		return _areasToGet;
	}

	public boolean hasAllAreas(User user)
	{
		for (int area : _areasToGet)
			if (!user.hasArea(area))
				return false;

		return true;
	}

	public void exchange()
	{
		Entry<User, Integer> highestBidder = null;
		for (Entry<User, Integer> bidder : _bidders.entrySet())
			if (highestBidder == null || bidder.getValue() > highestBidder.getValue())
				highestBidder = bidder;
		for (int area : _areasToGet)
		{
			_requester.getAreas().put(area, highestBidder.getKey().getAreas().get(area));
			highestBidder.getKey().getAreas().remove(area);
		}
		for (int area : _areasToGive)
		{
			highestBidder.getKey().getAreas().put(area, _requester.getAreas().get(area));
			_requester.getAreas().remove(area);
		}
		if (highestBidder.getValue() > 0)
		{
			highestBidder.getKey().decMoney(highestBidder.getValue());
			_requester.incMoney(highestBidder.getValue());
		}
	}
}