package windows;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import client.Client;

public class ChatRoom extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final Client _client;
	private final JTextField _sender = new JTextField();
	private final JTextArea _chat = new JTextArea();

	public ChatRoom(Client client)
	{
		super("צ'אט");

		_client = client;

		getContentPane().setBackground(Color.BLUE);
		setSize(450, 270);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		final Font font = new Font("Arial", Font.BOLD, 15);
		_chat.setBounds(10, 10, 420, 180);
		_chat.setFont(font);
		_chat.setRows(10);
		_chat.setLineWrap(true);
		_chat.setWrapStyleWord(true);
		_chat.setEditable(false);
		_chat.setBorder(LineBorder.createBlackLineBorder());
		_chat.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		add(_chat);
		_sender.setBorder(LineBorder.createBlackLineBorder());
		_sender.addKeyListener(new KeyboardChatSendListener());
		_sender.setBounds(130, 200, 300, 30);
		_sender.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		add(_sender);
		final JButton send = new JButton("שלח");
		send.addMouseListener(new MouseChatSendListener());
		send.setBorder(LineBorder.createBlackLineBorder());
		send.setBounds(10, 200, 110, 30);
		add(send);
	}

	public JTextArea getChatWindow()
	{
		return _chat;
	}

	private void sendText()
	{
		if (_sender.getText().trim().isEmpty())
			return;
		if (_sender.getText().length() > 25 && !_sender.getText().contains(" "))
			return;

		if (_sender.getText().length() < 25)
		{
			_client.getConnection().sendMessage(_client.getName(), _sender.getText());
			_sender.setText("");
		}
		else
		{
			String[] arr = _sender.getText().split(" ");
			String msg = "";
			for (int i = 0;i < arr.length;i++)
			{
				msg += arr[i] + " ";
				if (msg.length() > 25)
				{
					_client.getConnection().sendMessage(_client.getName(), msg.trim());
					msg = "";
				}
			}
			if (!msg.isEmpty())
				_client.getConnection().sendMessage(_client.getName(), msg.trim());

			_sender.setText("");
		}
	}

	private class KeyboardChatSendListener extends KeyAdapter
	{
		@Override
		public void keyTyped(KeyEvent ke)
		{
			if (ke.getKeyChar() == KeyEvent.VK_ENTER)
				sendText();
		}
	}

	private class MouseChatSendListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent me)
		{
			sendText();
		}
	}
}