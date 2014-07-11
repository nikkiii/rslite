package org.rslite.loader;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * A basic JPanel with an animated status icon.
 *
 * @author Nikki
 */
public class LoadingPanel extends JPanel {
	public LoadingPanel(Loader loader) {
		setPreferredSize(new Dimension(765, 503));
		setLayout(new BorderLayout());

		setBackground(Color.WHITE);

		JLabel headingLabel = new JLabel();
		headingLabel.setText("Loading world " + loader.getCurrentWorld() + "...");
		headingLabel.setHorizontalAlignment(JLabel.CENTER);
		add(headingLabel, java.awt.BorderLayout.NORTH);

		JLabel imageLabel = new JLabel();
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/loading1.gif"));
		imageLabel.setIcon(icon);
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		add(imageLabel, java.awt.BorderLayout.CENTER);

		setVisible(true);
	}
}
