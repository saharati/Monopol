package objects.cards;

public abstract class MasterCard
{
	private final int _id;
	private final String _name;

	public MasterCard(int id, String name)
	{
		_id = id;
		_name = name;
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public boolean isArea()
	{
		return false;
	}

	public boolean isBus()
	{
		return false;
	}

	public boolean isBusiness()
	{
		return false;
	}

	public int getPrice()
	{
		return 200;
	}
}