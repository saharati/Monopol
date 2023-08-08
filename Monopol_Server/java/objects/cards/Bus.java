package objects.cards;

public class Bus extends MasterCard
{
	public Bus(int id, String name)
	{
		super(id, name);
	}

	@Override
	public boolean isBus()
	{
		return true;
	}
}