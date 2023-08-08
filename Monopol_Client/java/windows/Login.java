package windows;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import client.Client;

public class Login extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final String LETTERS = "קראטוןםפףךלחיעכגדשץתצמנהבסז'";
	private final JTextField _name = new JTextField()
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected void processKeyEvent(KeyEvent e)
		{
			if (LETTERS.contains(String.valueOf(e.getKeyChar())) || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				super.processKeyEvent(e);
		}
	};

	public Login()
	{
		super("מונופול");

		getContentPane().setBackground(Color.BLUE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		setSize(220, 175);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);

		Font font = new Font("Arial", Font.BOLD, 50);

		final JLabel title = new JLabel("מונופול", JLabel.CENTER);
		title.setFont(font);
		title.setForeground(Color.ORANGE);
		title.setBounds(27, 15, 150, 50);
		add(title);

		font = new Font("Arial", Font.BOLD, 15);

		final KeyboardLoginListener keylistener = new KeyboardLoginListener();
		final JLabel username = new JLabel("שם בעברית:");
		username.setFont(font);
		username.setForeground(Color.WHITE);
		username.setBounds(118, 80, 80, 20);
		add(username);
		_name.setBorder(LineBorder.createBlackLineBorder());
		_name.addKeyListener(keylistener);
		_name.setBounds(8, 80, 100, 20);
		_name.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		add(_name);
		final JButton login = new JButton("כניסה");
		login.addMouseListener(new MouseLoginListener());
		login.setBounds(8, 110, 100, 20);
		add(login);
	}

	private void submitForm()
	{
		if (_name.getText().isEmpty())
			JOptionPane.showMessageDialog(null, "הזן שם בעברית.", "הכניסה נכשלה", JOptionPane.ERROR_MESSAGE);
		else if (_name.getText().length() > 10)
			JOptionPane.showMessageDialog(null, "מקסימום 10 אותיות בשם.", "הכניסה נכשלה", JOptionPane.ERROR_MESSAGE);
		else
		{
			try
			{
				new Client(new Socket(InetAddress.getByName("127.0.0.1"), 7777), this, _name.getText()).getConnection().login(_name.getText());
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, "השרת לא פעיל כרגע, נסה מאוחר יותר.", "שרת מכובה", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
 
	private class KeyboardLoginListener extends KeyAdapter
	{
		@Override
		public void keyTyped(KeyEvent ke)
		{
			if (ke.getKeyChar() == KeyEvent.VK_ENTER)
				submitForm();
		}
	}

	private class MouseLoginListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent me)
		{
			submitForm();
		}
	}

	public static void main(String[] args)
	{
		new Login().setVisible(true);
	}
}