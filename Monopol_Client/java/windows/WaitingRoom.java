package windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import client.Client;

public class WaitingRoom extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final JTable _users = new JTable()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int r, int c)
		{
			return false;
		}
	};
	private final Client _client;

	public WaitingRoom(Client client)
	{
		super("מונופול");

		_client = client;

		_users.setIntercellSpacing(new Dimension(20, 1));
		_users.setRowHeight(_users.getRowHeight() + 10);
		_users.getTableHeader().setBackground(Color.PINK);
		_users.getTableHeader().setReorderingAllowed(false);

		final JScrollPane tablePanel = new JScrollPane(_users);
		tablePanel.setBounds(0, 0, 450, 400);
		add(tablePanel);

		if (_client.getName().equalsIgnoreCase("סהר"))
		{
			final JButton start = new JButton("התחל");
			start.setBounds(165, 410, 120, 30);
			start.addMouseListener(new Start());
			add(start);

			setSize(454, 478);
		}
		else
			setSize(454, 428);

		addWindowListener(new Disco());
		setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void reloadTable(String[][] newData)
	{
		final DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

		_users.setModel(new DefaultTableModel(newData, new String[] {"שם"}));
		_users.getColumn("שם").setCellRenderer(centerAlign);
	}

	private class Disco extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	}

	private class Start extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			_client.getConnection().requestBattleStart();
		}
	}
}