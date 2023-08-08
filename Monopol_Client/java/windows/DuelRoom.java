package windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import client.Client;

public class DuelRoom extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final int[][] INDEXES =
	{
		{744, 753},
		{675, 756},
		{603, 757},
		{540, 759},
		{473, 755},
		{403, 754},
		{335, 757},
		{268, 758},
		{198, 761},
		{131, 758},
		{37, 757},
		{32, 672},
		{35, 611},
		{34, 541},
		{31, 477},
		{33, 408},
		{34, 342},
		{34, 276},
		{30, 202},
		{31, 139},
		{25, 41},
		{132, 32},
		{198, 34},
		{266, 35},
		{335, 34},
		{399, 34},
		{466, 36},
		{537, 39},
		{606, 37},
		{672, 35},
		{745, 37},
		{769, 136},
		{752, 199},
		{762, 271},
		{762, 343},
		{759, 402},
		{764, 475},
		{761, 541},
		{761, 606},
		{763, 674}
	};
	private static final int[][] HOUSES =
	{
		{733, 731},
		{664, 734},
		{598, 737},
		{529, 737},
		{463, 736},
		{396, 735},
		{330, 738},
		{261, 738},
		{192, 737},
		{124, 738},
		{16, 738},
		{19, 669},
		{18, 602},
		{20, 530},
		{20, 465},
		{20, 397},
		{20, 332},
		{18, 263},
		{19, 195},
		{21, 127},
		{23, 20},
		{126, 18},
		{191, 18},
		{257, 17},
		{327, 16},
		{389, 17},
		{457, 20},
		{526, 19},
		{596, 20},
		{664, 20},
		{733, 19},
		{736, 128},
		{733, 194},
		{734, 264},
		{733, 330},
		{733, 399},
		{734, 466},
		{735, 531},
		{734, 596},
		{735, 667}
	};

	private final List<Integer> _selectedAreas = new CopyOnWriteArrayList<Integer>();
	private final Map<Color, int[]> _points = new ConcurrentHashMap<Color, int[]>();
	private final Map<Color, int[][]> _areas = new ConcurrentHashMap<Color, int[][]>();
	private final JTable _users = new JTable()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int r, int c)
		{
			return false;
		}
	};
	private final JPanel _board = new JPanel(null)
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			final Graphics2D g2d = (Graphics2D) g;
			final Map<Color, int[]> copy = new ConcurrentHashMap<Color, int[]>(_points);
			g2d.drawImage(new ImageIcon(getClass().getResource("/images/board.jpg")).getImage(), 0, 0, 836, 832, DuelRoom.this);
			while (!copy.isEmpty())
			{
				final int[] points = copy.values().iterator().next();
				int x = points[0];
				int y = points[1];
				for (Entry<Color, int[]> e : copy.entrySet())
				{
					if (e.getValue()[0] != points[0] || e.getValue()[1] != points[1])
						continue;

					g2d.setColor(e.getKey());
					g2d.fillOval(x, y, 20, 20);
					g2d.setColor(Color.BLACK);
					g2d.drawOval(x, y, 20, 20);

					x += 23;
					if (x > e.getValue()[0] + 50)
					{
						x = e.getValue()[0];
						y += 23;
					}

					copy.remove(e.getKey());
				}
			}
			for (Entry<Color, int[][]> areas : _areas.entrySet())
			{
				for (int[] area : areas.getValue())
				{
					final int[] point = HOUSES[area[0]];

					g2d.setColor(areas.getKey());
					g2d.fillRect(point[0], point[1], 20, 20);
					g2d.setColor(Color.BLACK);
					g2d.drawRect(point[0], point[1], 20, 20);

					if (_selectedAreas.contains(area[0]))
					{
						g2d.setFont(new Font("Arial", Font.BOLD, 15));
						g2d.drawString("$", point[0] + 7, point[1] + 16);
					}
					else if (area[1] == 0)
						g2d.fillRect(point[0] + 5, point[1] + 5, 10, 10);
					else
					{
						g2d.setFont(new Font("Arial", Font.BOLD, 15));
						g2d.drawString(String.valueOf(area[1]), point[0] + 7, point[1] + 16);
					}
				}
			}
		}
	};
	private final Client _client;
	private final ScheduledThreadPoolExecutor _pool = new ScheduledThreadPoolExecutor(100);
	private final ScheduledThreadPoolExecutor _pool2 = new ScheduledThreadPoolExecutor(100);
	private final JButton _throwCubes = new JButton("זרוק קוביות");
	private final JButton _pay = new JButton("שלם 100");
	private final JLabel _cube1 = new JLabel();
	private final JLabel _cube2 = new JLabel();
	private final JLabel _card = new JLabel();
	private final JButton _question = new JButton();
	private final JPanel _sell = new JPanel(null);
	private final JScrollPane _offersTable;
	private final JPanel _order = new JPanel(null);
	private final JLabel _warning = new JLabel();
	private final JTable _offers = new JTable()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int r, int c)
		{
			return false;
		}
	};
	private ScheduledFuture<?> _thread;
	private ScheduledFuture<?> _thread2;
	private int _cardId;

	public DuelRoom(Client client)
	{
		super("מונופול");

		_client = client;

		_users.setIntercellSpacing(new Dimension(20, 1));
		_users.setRowHeight(_users.getRowHeight() + 10);
		_users.getTableHeader().setBackground(Color.PINK);
		_users.getTableHeader().setReorderingAllowed(false);
		_users.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		final JScrollPane tablePanel = new JScrollPane(_users);
		tablePanel.setBounds(0, 0, 836, 80);
		add(tablePanel);

		_offers.setIntercellSpacing(new Dimension(20, 1));
		_offers.setRowHeight(_offers.getRowHeight() + 10);
		_offers.getTableHeader().setBackground(Color.PINK);
		_offers.getTableHeader().setReorderingAllowed(false);
		_offers.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		_offersTable = new JScrollPane(_offers);
		_offersTable.setVisible(false);
		_board.add(_offersTable);

		_board.setBounds(0, 80, 836, 832);
		add(_board);

		final Font font = new Font("Arial", Font.BOLD, 15);
		final Font smallFont = new Font("Arial", Font.BOLD, 10);
		_throwCubes.setFont(smallFont);
		_throwCubes.setBounds(343, 401, 150, 30);
		_throwCubes.setVisible(false);
		_throwCubes.addMouseListener(new RollCubes());
		_board.add(_throwCubes);
		_pay.setFont(smallFont);
		_pay.setBounds(343, 431, 150, 30);
		_pay.setVisible(false);
		_pay.addMouseListener(new Pay());
		_board.add(_pay);

		_warning.setFont(font);
		_warning.setBounds(300, 670, 350, 30);
		_warning.setForeground(Color.RED);
		_warning.setVisible(false);
		_board.add(_warning);

		final JLabel question = new JLabel("הצע יותר:");
		question.setBounds(190, 0, 60, 30);
		question.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		_order.add(question);
		final JTextField more = new JTextField()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void processKeyEvent(KeyEvent e)
			{
				if (Character.isDigit(e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
					super.processKeyEvent(e);
			}
		};
		more.setBounds(120, 0, 70, 30);
		_order.add(more);
		final JButton accept = new JButton("שלח");
		accept.setBounds(60, 0, 60, 30);
		accept.setFont(smallFont);
		accept.addMouseListener(new PlaceBid(more));
		_order.add(accept);
		final JButton cancel = new JButton("בטל");
		cancel.setBounds(0, 0, 60, 30);
		cancel.setFont(smallFont);
		cancel.addMouseListener(new CancelBid());
		_order.add(cancel);
		_order.setVisible(false);
		_board.add(_order);

		final JButton bank = new JButton("בנק");
		bank.setFont(smallFont);
		bank.setBounds(0, 0, 80, 30);
		bank.addMouseListener(new SellBank());
		final JButton players = new JButton("שחקנים");
		players.setFont(smallFont);
		players.setBounds(80, 0, 80, 30);
		players.addMouseListener(new ViewButtons());
		final JTextField field = new JTextField("0")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void processKeyEvent(KeyEvent e)
			{
				if (Character.isDigit(e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
					super.processKeyEvent(e);
			}
		};
		field.setFont(smallFont);
		field.setBounds(0, 0, 100, 30);
		field.setVisible(false);
		final JButton send = new JButton("שלח");
		send.setFont(smallFont);
		send.setBounds(100, 0, 60, 30);
		send.setVisible(false);
		send.addMouseListener(new SendDeal());
		_sell.add(bank);
		_sell.add(players);
		_sell.add(field);
		_sell.add(send);
		_sell.setVisible(false);
		_board.add(_sell);

		_cube1.setFont(font);
		_cube1.setBackground(Color.WHITE);
		_cube1.setHorizontalAlignment(JLabel.CENTER);
		_cube1.setOpaque(true);
		_cube1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		_cube1.setBounds(379, 401, 30, 30);
		_cube1.setVisible(false);
		_board.add(_cube1);
		_cube2.setFont(font);
		_cube2.setBackground(Color.WHITE);
		_cube2.setHorizontalAlignment(JLabel.CENTER);
		_cube2.setOpaque(true);
		_cube2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		_cube2.setBounds(419, 401, 30, 30);
		_cube2.setVisible(false);
		_board.add(_cube2);

		_card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		_card.setBounds(350, 285, 190, 295);
		_card.setVisible(false);
		_board.add(_card);
		_question.setFont(smallFont);
		_question.setBounds(350, 580, 190, 30);
		_question.setVisible(false);
		_question.addMouseListener(new Purchase());
		_board.add(_question);

		setSize(836, 942);
		addMouseListener(new Click());
		addWindowListener(new Disco());
		setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void reloadTable(Object[][] newData)
	{
		_users.setModel(new DefaultTableModel(newData, new String[] {"שם", "כסף", "תור", "כלא", "התעלם"}));

		boolean hideCard = false;
		for (Object[] obj : newData)
		{
			if ((Boolean) obj[2])
			{
				hideCard = true;
				if (obj[0].toString().equals(_client.getName()))
				{
					_throwCubes.setText("זרוק קוביות");
					_throwCubes.setVisible(true);
					if ((Boolean) obj[3] && (Integer) obj[1] >= 100)
						_pay.setVisible(true);

					if (_thread != null)
						_thread.cancel(true);
					_thread = _pool.scheduleAtFixedRate(new Throw(), 0, 1, TimeUnit.SECONDS);
				}
			}
		}
		if (hideCard)
			_card.setVisible(false);

		final Enumeration<TableColumn> cols = _users.getColumnModel().getColumns();
		final ColoredTableCellRenderer renderer = new ColoredTableCellRenderer();
		while (cols.hasMoreElements())
			cols.nextElement().setCellRenderer(renderer);
	}

	public void warn(boolean warn, int turns)
	{
		if (turns == 3)
		{
			JOptionPane.showMessageDialog(null, "אין לך כסף יאפס.", "הפסדת", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		if (warn)
		{
			_warning.setText("אזהרה! תפשוט רגל בעוד " + (3 - turns) + " סבבים אם תשאר במינוס!");
			if (!_warning.isVisible())
				_warning.setVisible(true);
		}
		else if (_warning.isVisible())
			_warning.setVisible(false);
	}

	public void reloadBoard(Object[][] newData)
	{
		_points.clear();
		_areas.clear();
		for (Object[] obj : newData)
		{
			_points.put((Color) obj[0], INDEXES[(Integer) obj[1]]);
			_areas.put((Color) obj[0], (int[][]) obj[2]);
		}
		_board.repaint();
	}

	public void throwCubes()
	{
		_cube1.setVisible(true);
		_cube2.setVisible(true);

		if (_thread != null)
			_thread.cancel(true);
		_thread = _pool.scheduleAtFixedRate(new Cubes(), 0, 50, TimeUnit.MILLISECONDS);
	}

	public void endCubes(int cube1, int cube2)
	{
		_thread.cancel(true);
		_thread = _pool.schedule(new SetNumbs(cube1, cube2), 100, TimeUnit.MILLISECONDS);
	}

	public void showCard(int cardId, int price, boolean buyable, boolean upgrade, int money, String name)
	{
		_cardId = cardId;

		if (cardId > 0)
		{
			String path;
			if (cardId < 40)
			{
				path = "areas";
				if (name.equals(_client.getName()) && buyable)
				{
					if (price > money)
					{
						_question.setText("אין מספיק כסף");
						_question.setEnabled(false);
					}
					else
					{
						_question.setText((upgrade ? "שדרג" : "קנה") + " ב- " + price + "? (7)");
						_question.setEnabled(true);
					}
					_question.setVisible(true);

					_thread.cancel(true);
					_thread = _pool.scheduleAtFixedRate(new CountDown(7, price, upgrade, name), 1, 1, TimeUnit.SECONDS);
				}
				else
				{
					_thread.cancel(true);
					_thread = _pool.scheduleAtFixedRate(new CountDown(3, price, upgrade, name), 1, 1, TimeUnit.SECONDS);
				}
			}
			else
			{
				if (cardId > 49 && cardId < 70)
					path = "surprises";
				else
					path = "commands";

				if (cardId != 51 && cardId != 93)
				{
					_thread.cancel(true);
					_thread = _pool.scheduleAtFixedRate(new CountDown(3, price, upgrade, name), 1, 1, TimeUnit.SECONDS);
				}
			}

			_card.setIcon(new ImageIcon(getClass().getResource("/images/" + path + "/" + cardId + ".png")));
			_card.setVisible(true);
		}
		else
		{
			_thread.cancel(true);
			_thread = _pool.scheduleAtFixedRate(new CountDown(3, price, upgrade, name), 1, 1, TimeUnit.SECONDS);
		}

		_cube1.setVisible(false);
		_cube2.setVisible(false);
	}

	public void showSellDialog(int index)
	{
		_selectedAreas.clear();
		_selectedAreas.add(index);
		_board.repaint();

		int x = HOUSES[index][0];
		int y = HOUSES[index][1];
		if (index > 0 && index < 20)
			y -= 30;
		else if (index > 20 && index < 30)
		{
			x -= 5;
			y += 90;
		}
		else if (index > 30 && index < 40)
		{
			x -= 120;
			y -= 30;
		}

		_sell.getComponent(0).setVisible(true);
		_sell.getComponent(1).setVisible(true);
		_sell.getComponent(2).setVisible(false);
		_sell.getComponent(3).setVisible(false);
		_sell.setBounds(x, y, 160, 30);
		_sell.setVisible(true);
	}

	public void placeBid(String requester, int wantedMoney, String[] areasToGive, String[] areasToGet)
	{
		final int extraSize = wantedMoney > 0 ? 1 : 0;
		final int size = (areasToGive.length > areasToGet.length ? areasToGive.length : areasToGet.length) + extraSize;
		final String[][] data = new String[size][2];
		for (int i = 0;i < areasToGive.length;i++)
			data[i][0] = areasToGive[i];
		if (wantedMoney > 0)
			data[0][1] = wantedMoney + " שקל";
		for (int i = extraSize;i < areasToGet.length + extraSize;i++)
			data[i][1] = areasToGet[i - extraSize];

		_offers.setBounds(150, 150, 250, _offers.getRowHeight() * (data.length + 1));
		_offers.setModel(new DefaultTableModel(data, new String[] {requester + " מציע", "תמורת"}));
		_offersTable.setBounds(150, 150, 250, _offers.getRowHeight() * (data.length + 1));
		_offersTable.setVisible(true);
		_order.getComponent(2).setEnabled(!requester.equals(_client.getName()));
		_order.getComponent(3).setEnabled(requester.equals(_client.getName()));
		((JTextField) _order.getComponent(1)).setText(String.valueOf(wantedMoney));

		_order.setBounds(150, 150 + _offers.getRowHeight() * (data.length + 1), 250, 30);
		_order.setVisible(true);

		if (requester.equals(_client.getName()))
			_thread2 = _pool2.scheduleAtFixedRate(new EndBid(20), 0, 1, TimeUnit.SECONDS);
	}

	public void cancelBid()
	{
		_offersTable.setVisible(false);
		_order.setVisible(false);

		if (_thread2 != null)
			_thread2.cancel(true);
	}

	public void onBid(int time)
	{
		if (time == 0)
		{
			_offersTable.setVisible(false);
			_order.setVisible(false);

			if (_thread2 != null)
				_thread2.cancel(true);
		}
		else
			((JLabel) _order.getComponent(0)).setText("(" + time + ") הצע יותר:");
	}

	private class ColoredTableCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (table.getRowCount() > row && table.getColumnCount() > 4)
				setBackground((Color) _users.getValueAt(row, 4));

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

		@Override
		public int getHorizontalAlignment()
		{
			return CENTER;
		}
	}

	private class Disco extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	}

	private class PlaceBid extends MouseAdapter
	{
		private final JTextField _field;

		private PlaceBid(JTextField field)
		{
			_field = field;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (!_order.getComponent(2).isEnabled())
				return;

			_order.getComponent(2).setEnabled(false);
			_order.getComponent(3).setEnabled(true);
			_client.getConnection().sendBid(Integer.valueOf(_field.getText()));
		}
	}

	private class CancelBid extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			if (!_order.getComponent(3).isEnabled())
				return;

			_order.getComponent(2).setEnabled(true);
			_order.getComponent(3).setEnabled(false);
			_client.getConnection().cancelBid();
		}
	}

	private class RollCubes extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			_thread.cancel(true);
			_throwCubes.setVisible(false);
			_pay.setVisible(false);
			_client.getConnection().throwCubes();
		}
	}

	private class Pay extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			_thread.cancel(true);
			_throwCubes.setVisible(false);
			_pay.setVisible(false);
			_client.getConnection().pay();
		}
	}

	private class Cubes implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				_cube1.setText(String.valueOf(new Random().nextInt(6) + 1));
				_cube2.setText(String.valueOf(new Random().nextInt(6) + 1));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class Throw implements Runnable
	{
		private int _count = 7;

		@Override
		public void run()
		{
			try
			{
				_count--;
				if (_count <= 3)
				{
					if (_count == 0)
					{
						_throwCubes.setVisible(false);
						_pay.setVisible(false);
						_client.getConnection().throwCubes();
						_thread.cancel(true);
					}
					else
						_throwCubes.setText("זרוק קוביות (" + _count + ")");
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class SetNumbs implements Runnable
	{
		private final int _num1;
		private final int _num2;

		private SetNumbs(int cube1, int cube2)
		{
			_num1 = cube1;
			_num2 = cube2;
		}

		@Override
		public void run()
		{
			try
			{
				_cube1.setText(String.valueOf(_num1));
				_cube2.setText(String.valueOf(_num2));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class EndBid implements Runnable
	{
		private int _count;

		private EndBid(int count)
		{
			_count = count;
		}

		@Override
		public void run()
		{
			_client.getConnection().onBid(--_count);
		}
	}

	private class CountDown implements Runnable
	{
		private int _count;
		private final int _price;
		private final boolean _upgrade;
		private final String _name;

		private CountDown(int count, int price, boolean upgrade, String name)
		{
			_count = count;
			_price = price;
			_upgrade = upgrade;
			_name = name;
		}

		@Override
		public void run()
		{
			try
			{
				_count--;
				if (_question.isVisible() && _question.isEnabled())
					_question.setText((_upgrade ? "שדרג" : "קנה") + " ב- " + _price + "? (" + _count + ")");
				if (_count == 0)
				{
					_card.setVisible(false);
					if (_question.isVisible())
						_question.setVisible(false);
					if (_name.equals(_client.getName()))
						_client.getConnection().purchase(-1);
					_thread.cancel(true);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class Purchase extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			if (!_question.isEnabled())
				return;

			_card.setVisible(false);
			_question.setVisible(false);
			_client.getConnection().purchase(_cardId);
			_thread.cancel(true);
		}
	}

	private class Click extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			boolean found = false;
			for (int i = 0;i < HOUSES.length;i++)
			{
				if (e.getX() > HOUSES[i][0] && e.getX() < HOUSES[i][0] + 20 && e.getY() > HOUSES[i][1] + 100 && e.getY() < HOUSES[i][1] + 100 + 20)
				{
					if (!_selectedAreas.isEmpty())
					{
						_selectedAreas.add(i);
						_board.repaint();
					}
					else
						_client.getConnection().sell(i);

					found = true;
					break;
				}
			}
			if (!found && _sell.isVisible())
			{
				_selectedAreas.clear();
				_sell.setVisible(false);
				_board.repaint();
			}
		}
	}

	private class SellBank extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			_sell.setVisible(false);
			_client.getConnection().requestSellTrade("Bank", -1, _selectedAreas.toArray(new Integer[_selectedAreas.size()]));
			_selectedAreas.clear();
			_board.repaint();
		}
	}

	private class ViewButtons extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			_sell.getComponent(0).setVisible(false);
			_sell.getComponent(1).setVisible(false);
			_sell.getComponent(2).setVisible(true);
			_sell.getComponent(3).setVisible(true);
		}
	}

	private class SendDeal extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			final String text = ((JTextField) _sell.getComponent(2)).getText();
			final int price = text.isEmpty() ? -1 : Integer.valueOf(text);

			_sell.setVisible(false);
			_client.getConnection().requestSellTrade("Players", price, _selectedAreas.toArray(new Integer[_selectedAreas.size()]));
			_selectedAreas.clear();
			_board.repaint();
		}
	}
}