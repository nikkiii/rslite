package org.rslite;

import org.rslite.worldselector.GameSelect;

import javax.swing.UnsupportedLookAndFeelException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main entry class for RSLite
 *
 * @author Nikki
 */
public class RSLite {

	private static final Logger logger = Logger.getLogger(RSLite.class.getName());

	/**
	 * Main entry point for RSLite
	 *
	 * @param args Command line arguments
	 */
	public static void main(String args[]) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GameSelect().setVisible(true);
			}
		});
	}
}
