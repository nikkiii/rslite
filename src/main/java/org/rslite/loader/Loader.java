package org.rslite.loader;

import org.rslite.jagex.JagexConfiguration;
import org.rslite.jagex.JagexConfigurationParser;

import javax.swing.JFrame;
import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Loader {

	/**
	 * The Loader Configuration
	 */
	private final LoaderConfig config;

	/**
	 * The target world
	 */
	private final int world;

	/**
	 * The Applet loader
	 */
	private WebAppletLoader loader;

	/**
	 * The Applet instance
	 */
	private Applet applet;

	/**
	 * The Main JFrame
	 */
	private JFrame frame;

	/**
	 * The parsed jav_config
	 */
	private JagexConfiguration jagConfig;

	public Loader(LoaderConfig config, int world) {
		this.config = config;
		this.world = world;
	}

	/**
	 * Load and parse the java configuration
	 *
	 * @throws IOException If a connection error occurred
	 */
	public void load() throws IOException {
		JagexConfigurationParser parser = new JagexConfigurationParser(new URL(world != -1 ? config.getUrlForWorld(world) : config.getAutoUrl()));
		jagConfig = parser.parse();
		loader = new WebAppletLoader(jagConfig);
		loader.load();
	}

	/**
	 * Initialize the JFrame and start the loading of the applet.
	 *
	 * This is threaded so that it doesn't block the loading panel.
	 */
	public void initFrame() {
		final LoadingPanel loadingPanel = new LoadingPanel(this);

		frame = new JFrame((String) loader.getConfiguration().get("title"));
		frame.add(loadingPanel);
		frame.setResizable(config.isResizable());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					applet = loader.newApplet();
				} catch (AppletLoaderException e) {
					e.printStackTrace();
				}

				frame.remove(loadingPanel);
				frame.add(applet);
				frame.pack();
			}
		}).start();
	}

	/**
	 * Get the current loaded world
	 * Note: This is pretty hacky, since it matches the number out of the url.
	 * @return The loaded world id (it could be either oldschool 1-* or rs3 1-*)
	 */
	public int getCurrentWorld() {
		// We could use jagConfig here, but the values may change and we may get junk.
		Matcher m = Pattern.compile("(\\d+)\\.").matcher(jagConfig.get("codebase").toString());
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return -1;
	}
}
