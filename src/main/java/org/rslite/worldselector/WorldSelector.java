package org.rslite.worldselector;

import org.rslite.RSLiteConfig;
import org.rslite.jagex.world.Location;
import org.rslite.jagex.world.WorldInfo;
import org.rslite.jagex.world.WorldParser;
import org.rslite.loader.Loader;
import org.rslite.loader.LoaderConfigs;
import org.rslite.util.LatencyChecker;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WorldSelector extends javax.swing.JFrame {

	private static final Color FAVORITE_COLOR = new Color(237, 218, 116);

	/**
	 * Preloaded location icons for the world list table
	 */
	private static final ImageIcon[] LOCATION_ICONS = new ImageIcon[] {
			new ImageIcon(WorldSelector.class.getResource("/flags/us.png")),
			new ImageIcon(WorldSelector.class.getResource("/flags/gb.png")),
			new ImageIcon(WorldSelector.class.getResource("/flags/ca.png")),
			new ImageIcon(WorldSelector.class.getResource("/flags/au.png")),
			new ImageIcon(WorldSelector.class.getResource("/flags/nl.png")),
			new ImageIcon(WorldSelector.class.getResource("/flags/se.png")),
			new ImageIcon(WorldSelector.class.getResource("/flags/fi.png")),
			new ImageIcon(WorldSelector.class.getResource("/flags/de.png"))
	};

	private static final WorldParser parser = new WorldParser();

	private static final Random random = new Random();

	private DefaultTableModel worldTableModel;

	private List<WorldInfo> worldList;
	private ScheduledFuture<?> updateFuture;
	private List<Integer> favorites;

	/**
	 * Creates new form WorldSelector
	 */
	public WorldSelector() {
		super("RSLite - World Select");

		try {
			setIconImage(ImageIO.read(this.getClass().getResource("/icon.png")));
		} catch (IOException e) {
			// Simply unable to load, meh.
		}

		initComponents();

		new Thread(new Runnable() {
			@Override
			public void run() {
				initWorldStatuses();
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		favorites = new ArrayList<>();

		if (RSLiteConfig.hasProperty("favorites")) {
			String[] strings = RSLiteConfig.getProperty("favorites").split(",");
			for(String s : strings) {
				favorites.add(Integer.parseInt(s));
			}
		}

		jScrollPane1 = new javax.swing.JScrollPane();

		worldTable = new javax.swing.JTable() {
			private Color basicColor = null;
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				if (basicColor == null) {
					basicColor = c.getBackground();
				}

				if (!isRowSelected(row)) {
					if (favorites.contains(getValueAt(row, 0))) {
						c.setBackground(FAVORITE_COLOR);
					} else {
						c.setBackground(basicColor);
					}
				}

				return c;
			}
		};

		playButton = new javax.swing.JButton();
		playRandomButton = new javax.swing.JButton();
		playBestButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		worldTable.setModel(worldTableModel = new javax.swing.table.DefaultTableModel(
				new Object[][]{

				},
				new String[]{
						"World", "Location", "Activity", "Players", "Special Info", "Response Time"
				}
		) {
			Class[] types = new Class[]{
					java.lang.Integer.class, javax.swing.ImageIcon.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class
			};

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			boolean[] canEdit = new boolean[]{
					false, false, false, false, false, false
			};

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		jScrollPane1.setViewportView(worldTable);

		for (int i = 0; i < worldTable.getColumnModel().getColumnCount(); i++) {
			TableColumn column = worldTable.getColumnModel().getColumn(i);
			column.setResizable(false);
		}

		if (worldTable.getColumnModel().getColumnCount() > 0) {
			worldTable.getColumnModel().getColumn(0).setResizable(false);
			worldTable.getColumnModel().getColumn(0).setPreferredWidth(20);
			worldTable.getColumnModel().getColumn(1).setResizable(false);
			worldTable.getColumnModel().getColumn(1).setPreferredWidth(30);
			worldTable.getColumnModel().getColumn(2).setResizable(false);
			worldTable.getColumnModel().getColumn(2).setPreferredWidth(200);
			worldTable.getColumnModel().getColumn(3).setResizable(false);
			worldTable.getColumnModel().getColumn(3).setPreferredWidth(20);
			worldTable.getColumnModel().getColumn(4).setResizable(false);
			worldTable.getColumnModel().getColumn(5).setResizable(false);
			worldTable.getColumnModel().getColumn(5).setPreferredWidth(50);
		}

		// Add the favorite sorter
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(worldTableModel);
		worldTable.setRowSorter(sorter);

		sorter.setComparator(0, new Comparator<Integer>() {
			@Override
			public int compare(Integer worldId1, Integer worldId2) {
				if (favorites.contains(worldId1) && !favorites.contains(worldId2)) {
					return -1;
				} else if (!favorites.contains(worldId1) && favorites.contains(worldId2)) {
					return 1;
				}
				return worldId1.compareTo(worldId2);
			}
		});

		worldTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = worldTable.rowAtPoint(e.getPoint());

				if (r >= 0 && r < worldTable.getRowCount()) {
					worldTable.setRowSelectionInterval(r, r);
				} else {
					worldTable.clearSelection();
				}

				int rowIndex = worldTable.getSelectedRow();
				if (rowIndex < 0)
					return;

				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					JPopupMenu popup = createPopupMenu(rowIndex);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		playButton.setText("Play");
		playButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				playButtonActionPerformed(evt);
			}
		});

		playRandomButton.setText("Play on a random world");
		playRandomButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				playRandomButtonActionPerformed(evt);
			}
		});

		playBestButton.setText("Play on the best (lowest ping) server");
		playBestButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				playBestButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
												.addComponent(playBestButton, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
												.addComponent(playRandomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(jScrollPane1))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(playButton, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
												.addComponent(playBestButton, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
										.addComponent(playRandomButton, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
								.addContainerGap())
		);

		pack();
	}

	private JPopupMenu createPopupMenu(int row) {
		JPopupMenu popup = new JPopupMenu();

		final int world = (int) worldTable.getValueAt(row, 0);

		JMenuItem play = new JMenuItem("Play");
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();

				startClient(world);
			}
		});
		popup.add(play);

		JMenuItem favorite = new JMenuItem();
		if (favorites.contains(world)) {
			favorite.setText("Remove from favorites");
			favorite.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					favorites.remove((Object) world);
					worldTable.getRowSorter().allRowsChanged();
					saveFavorites();
				}
			});
		} else {
			favorite.setText("Add to favorites");
			favorite.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					favorites.add(world);
					worldTable.getRowSorter().allRowsChanged();
					saveFavorites();
				}
			});
		}
		popup.add(favorite);
		return popup;
	}

	private void saveFavorites() {
		StringBuilder out = new StringBuilder();
		for (Iterator<Integer> it$ = favorites.iterator(); it$.hasNext();) {
			out.append(it$.next());
			if (it$.hasNext()) {
				out.append(',');
			}
		}
		RSLiteConfig.setProperty("favorites", out.toString());
	}

	/**
	 * Initialize the world statuses on the rows
	 */
	private void initWorldStatuses() {
		try {
			worldList = new LinkedList<>();

			// Load in standard worlds
			worldList.addAll(parser.parse());

			// Load in extra worlds
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/worlds.csv")))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] s = line.split(",");
					worldList.add(new WorldInfo(Integer.parseInt(s[0]), s[1], s[2], 0, Location.valueOf(s[3]), Boolean.parseBoolean(s[4]), Boolean.parseBoolean(s[5]), Boolean.parseBoolean(s[6])));
				}
			}

			for (WorldInfo info : worldList) {
				addWorld(info);
			}

			worldTable.getRowSorter().toggleSortOrder(0);

			ExecutorService pingService = Executors.newFixedThreadPool(2);

			// After it's populated let's start off pinging servers
			for (WorldInfo info : worldList) {
				pingService.execute(new LatencyCheckRunnable(info));
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to load the world list due to a connection error.\nPlease re-load RSLite and try again.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		updateFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				updateWorldStatuses();
			}
		}, 30, 30, TimeUnit.SECONDS);
	}

	private void addWorld(WorldInfo info) {
		StringBuilder extra = new StringBuilder();
		if (info.isPvp()) {
			extra.append("PvP");
		}
		if (info.isHighRisk()) {
			if (extra.length() > 0)
				extra.append(", ");

			extra.append("High Risk");
		}
		worldTableModel.addRow(new Object[]{
				info.getWorldId() - 300, // Get the actual world id
				getWorldLocation(info.getLocation()),
				info.getActivity(),
				info.getPlayerCount(),
				extra.toString(),
				-1
		});
	}

	/**
	 * Update the world status from the page
	 * This used to overwrite worldList, but it wouldn't let us have custom worlds.
	 */
	private void updateWorldStatuses() {
		if (!isActive()) {
			return; // It may have been disposed and not canceled yet.
		}
		try {
			List<WorldInfo> worldList = parser.parse();

			for (WorldInfo info : worldList) {
				int row = findWorldRow(info.getWorldId());

				if (row != -1) {
					worldTableModel.setValueAt(info.getPlayerCount(), row, 3);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find a row id by the world id
	 * @param worldId The world id to search for
	 * @return The row, or -1 if not found
	 */
	private int findWorldRow(int worldId) {
		if (worldId > 300) {
			worldId -= 300;
		}
		for (int i = 0; i < worldTableModel.getRowCount(); i++) {
			int id = (int) worldTable.getValueAt(i, 0);

			if (id == worldId) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the location icon for a world's location
	 * @param location The location to get the icon for
	 * @return The preloaded ImageIcon
	 */
	private ImageIcon getWorldLocation(Location location) {
		return LOCATION_ICONS[location.ordinal()];
	}

	/**
	 * Start the game client with the selected row as the world.
	 *
	 * @param evt A standard action event, useless for us.
	 */
	private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int row = worldTable.getSelectedRow();

		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Please select a world.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		dispose();

		int worldId = (int) worldTable.getValueAt(row, 0);

		RSLiteConfig.setProperty("lastWorld", worldId);

		startClient(worldId);
	}

	/**
	 * Start the game client with us choosing the lowest latency option
	 *
	 * @param evt A standard action event, useless for us.
	 */
	private void playBestButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int lowestId = -1;
		long lowestValue = -1;

		for (int i = 0; i < worldTableModel.getRowCount(); i++) {
			int value = (int) worldTable.getValueAt(i, 5);

			// Make sure it's not a pvp world. High risk are semi-ok I guess?
			if (!worldTable.getValueAt(i, 4).toString().contains("PvP") && (lowestValue == -1 || value != -1 && value < lowestValue)) {
				lowestValue = value;
				lowestId = (int) worldTable.getValueAt(i, 0);
			}
		}

		dispose();

		RSLiteConfig.setProperty("lastWorld", lowestId);

		if (lowestId != -1) {
			startClient(lowestId);
		}
	}

	/**
	 * Start the game client with the selected row as the world.
	 *
	 * @param evt A standard action event, useless for us.
	 */
	private void playRandomButtonActionPerformed(java.awt.event.ActionEvent evt) {
		WorldInfo info = null;

		// Choose a world until we find one that isn't pvp, high risk, or full.
		while (info == null || info.isPvp() || info.isHighRisk() || info.getPlayerCount() >= 2000) {
			info = worldList.get(random.nextInt(worldList.size()));
		}

		dispose();

		RSLiteConfig.setProperty("lastWorld", info.getWorldId() - 300);

		startClient(info.getWorldId());
	}

	/**
	 * Start the loader in a new thread
	 * @param world The world to load. This will automatically be converted from a 3** world to the oldschool format
	 */
	private void startClient(final int world) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Loader l = new Loader(LoaderConfigs.RUNESCAPE_OLDSCHOOL, world > 300 ? world - 300 : world);
					l.load();
					l.initFrame();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Unable to load Runescape due to a connection error.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}).start();
	}

	@Override
	public void dispose() {
		super.dispose();
		updateFuture.cancel(true);
	}

	// Variables declaration - do not modify
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton playBestButton;
	private javax.swing.JButton playButton;
	private javax.swing.JButton playRandomButton;
	private javax.swing.JTable worldTable;
	// End of variables declaration

	private class LatencyCheckRunnable implements Runnable {
		private WorldInfo info;

		public LatencyCheckRunnable(WorldInfo info) {
			this.info = info;
		}

		@Override
		public void run() {
			try {
				long latency = LatencyChecker.checkLatency(info.getHost(), 43594);

				int row = findWorldRow(info.getWorldId());

				worldTableModel.setValueAt((int) latency, row, 5);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}