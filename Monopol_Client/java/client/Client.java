package client;

import java.net.Socket;

import javax.swing.JFrame;

import network.PacketHandler;

public class Client
{
	private final PacketHandler _connection;
	private final String _name;
	private JFrame _window;
	private JFrame _chatRoom;

	public Client(Socket connection, JFrame window, String name)
	{
		_connection = new PacketHandler(this, connection);
		_window = window;
		_name = name;

		new ClientThread(this).start();
	}

	public PacketHandler getConnection()
	{
		return _connection;
	}

	public JFrame getWindow()
	{
		return _window;
	}

	public String getName()
	{
		return _name;
	}

	public void setWindow(JFrame window)
	{
		_window.setVisible(false);
		_window = window;
		_window.setVisible(true);
	}

	public JFrame getChatRoom()
	{
		return _chatRoom;
	}

	public void setChatRoom(JFrame chatRoom)
	{
		_chatRoom = chatRoom;
		_chatRoom.setVisible(true);
	}
}