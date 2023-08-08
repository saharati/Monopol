package objects.cards;

public class Area extends MasterCard
{
	private final int _price;
	private final int _priceHouse;
	private final int _priceHotel;
	private final int _m;
	private final int[] _costs;
	private final int[] _relatedIds;

	public Area(int id, String name, int price, int priceHouse, int priceHotel, int m, int[] costs, int[] relatedIds)
	{
		super(id, name);

		_price = price;
		_priceHouse = priceHouse;
		_priceHotel = priceHotel;
		_m = m;
		_costs = costs;
		_relatedIds = relatedIds;
	}

	@Override
	public boolean isArea()
	{
		return true;
	}

	@Override
	public int getPrice()
	{
		return _price;
	}

	public int getPriceHouse()
	{
		return _priceHouse;
	}

	public int getPriceHotel()
	{
		return _priceHotel;
	}

	public int getM()
	{
		return _m;
	}

	public int[] getCosts()
	{
		return _costs;
	}

	public int[] getRelatedIds()
	{
		return _relatedIds;
	}
}