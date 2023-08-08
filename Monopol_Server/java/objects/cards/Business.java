package objects.cards;

public class Business extends MasterCard
{
	public Business(int id, String name)
	{
		super(id, name);
	}

	@Override
	public boolean isBusiness()
	{
		return true;
	}
}