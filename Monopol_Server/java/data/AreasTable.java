package data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import objects.cards.Area;
import objects.cards.Bus;
import objects.cards.Business;
import objects.cards.MasterCard;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AreasTable extends HashMap<Integer, MasterCard>
{
	private static final long serialVersionUID = 1L;

	protected AreasTable()
	{
		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			final Document doc = factory.newDocumentBuilder().parse(new File("areas.xml"));
			for (Node n = doc.getFirstChild();n != null;n = n.getNextSibling())
			{
				if ("list".equals(n.getNodeName()))
				{
					for (Node d = n.getFirstChild();d != null;d = d.getNextSibling())
					{
						if ("card".equals(d.getNodeName()) || "bus".equals(d.getNodeName()) || "business".equals(d.getNodeName()))
						{
							final NamedNodeMap attrs = d.getAttributes();
							final int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							final String name = attrs.getNamedItem("name").getNodeValue();

							if ("card".equals(d.getNodeName()))
							{
								final int price = Integer.parseInt(attrs.getNamedItem("price").getNodeValue());
								final int priceHouse = Integer.parseInt(attrs.getNamedItem("priceHouse").getNodeValue());
								final int priceHotel = Integer.parseInt(attrs.getNamedItem("priceHotel").getNodeValue());
								final int m = Integer.parseInt(attrs.getNamedItem("m").getNodeValue());
								final int[] costs = new int[6];
								costs[0] = Integer.parseInt(attrs.getNamedItem("cost0").getNodeValue());
								costs[1] = Integer.parseInt(attrs.getNamedItem("cost1").getNodeValue());
								costs[2] = Integer.parseInt(attrs.getNamedItem("cost2").getNodeValue());
								costs[3] = Integer.parseInt(attrs.getNamedItem("cost3").getNodeValue());
								costs[4] = Integer.parseInt(attrs.getNamedItem("cost4").getNodeValue());
								costs[5] = Integer.parseInt(attrs.getNamedItem("cost5").getNodeValue());
								final String related = attrs.getNamedItem("relatedIds").getNodeValue();
								final int[] relatedIds = new int[related.split(",").length];
								int i = 0;
								for (String s : related.split(","))
									relatedIds[i++] = Integer.parseInt(s);
								put(id, new Area(id, name, price, priceHouse, priceHotel, m, costs, relatedIds));
							}
							else if ("bus".equals(d.getNodeName()))
								put(id, new Bus(id, name));
							else
								put(id, new Business(id, name));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<Area> getAreas()
	{
		final List<Area> areas = new ArrayList<Area>();
		for (MasterCard c : values())
			if (c.isArea())
				areas.add((Area) c);

		return areas;
	}

	public List<Bus> getBuses()
	{
		final List<Bus> buses = new ArrayList<Bus>();
		for (MasterCard c : values())
			if (c.isBus())
				buses.add((Bus) c);

		return buses;
	}

	public List<Business> getBusinesses()
	{
		final List<Business> busnisses = new ArrayList<Business>();
		for (MasterCard c : values())
			if (c.isBusiness())
				busnisses.add((Business) c);

		return busnisses;
	}

	public static final AreasTable getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		private static final AreasTable _instance = new AreasTable();
	}
}