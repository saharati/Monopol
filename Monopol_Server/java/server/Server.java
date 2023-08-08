package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import data.AreasTable;

public class Server
{
	public static void main(String[] args)
	{
		try
		{
			printSection("LOADING SERVER");
			AreasTable.getInstance();
			final ServerSocket listening = new ServerSocket(7777);
			System.out.println("Server listening on port: 7777 user: " + System.getProperty("user.name") + " hostname: " + InetAddress.getLocalHost() + ".");
			printSection("SERVER LOADED");
			while (true)
				new ServerThread(listening.accept()).start();
		}
		catch (IOException e)
		{
			System.out.println("Server failed to load, port is already in use.");
		}
	}

	private static void printSection(String section)
	{
		section = "=[ " + section + " ]";
		while (section.length() < 50)
			section = "-" + section;
		System.out.println(section);
	}
}