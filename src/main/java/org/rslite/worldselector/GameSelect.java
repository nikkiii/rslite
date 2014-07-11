package org.rslite.worldselector;

import org.rslite.RSLiteConfig;
import org.rslite.loader.Loader;
import org.rslite.loader.LoaderConfigs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The main entry frame, used to select the game type (Oldschool with last world, world select, or rs3)
 */
public class GameSelect extends javax.swing.JFrame {

	/**
	 * Creates new form GameSelect
	 */
	public GameSelect() {
		super("RSLite - Select Game");
		initComponents();
	}


	private void initComponents() {

		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jButton3 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		String extra = "Random World";

		if (RSLiteConfig.hasProperty("lastWorld")) {
			extra = "Last World: " + RSLiteConfig.getProperty("lastWorld");
		}

		jButton1.setText("Play OldSchool Runescape (" + extra + ")");

		jButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GameSelect.this.dispose();

				Loader loader = new Loader(LoaderConfigs.RUNESCAPE_OLDSCHOOL, RSLiteConfig.hasProperty("lastWorld") ? RSLiteConfig.getPropertyInt("lastWorld") : -1);
				try {
					loader.load();
					RSLiteConfig.setProperty("lastWorld", loader.getCurrentWorld());
					loader.initFrame();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		jButton2.setText("Play OldSchool Runescape (World Select)");

		jButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GameSelect.this.dispose();

				new WorldSelector().setVisible(true);
			}
		});

		jButton3.setText("Play Runescape 3");

		jButton3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GameSelect.this.dispose();

				Loader loader = new Loader(LoaderConfigs.RUNESCAPE_3, -1);
				try {
					loader.load();
					loader.initFrame();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pack();
	}// </editor-fold>

	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	// End of variables declaration
}