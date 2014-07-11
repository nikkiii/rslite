package org.rslite.loader;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import org.rslite.jagex.JagexConfiguration;

/**
 * Loads an applet from a page, copying all parameters
 *
 * @author Nikki
 *
 */
public class WebAppletLoader {

	/**
	 * The java configuration
	 */
	private final JagexConfiguration configuration;

	/**
	 * The code base
	 */
	private URL baseUrl;

	/**
	 * The archive url
	 */
	private URL archiveUrl;

	/**
	 * The main class name
	 */
	private String className;

	/**
	 * Initial dimensions
	 */
	private Dimension dimension = new Dimension(765, 503);

	public WebAppletLoader(JagexConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Load and set basic configuration options
	 * @throws IOException If an error occurred while creating the URLs
	 */
	public void load() throws IOException {
		baseUrl = new URL((String) configuration.get("codebase"));

		archiveUrl = new URL(baseUrl + (String) configuration.get("initial_jar"));

		className = configuration.get("initial_class");
		className = className.substring(0, className.lastIndexOf('.'));

		if (configuration.has("applet_minwidth")) {
			String widthAttr = configuration.get("applet_minwidth");
			dimension.width = Integer.parseInt(widthAttr);
		}

		if (configuration.has("applet_minheight")) {
			String heightAttr = configuration.get("applet_minheight");
			dimension.height = Integer.parseInt(heightAttr);
		}
	}

	/**
	 * Initialize a new applet instance
	 * @return The new applet
	 * @throws AppletLoaderException If an error occurred loading the archive, class, or any other part
	 */
	public Applet newApplet() throws AppletLoaderException {
		if(archiveUrl == null) {
			throw new IllegalArgumentException("No applet url!");
		}
		try {
			URLClassLoader localURLClassLoader = new URLClassLoader(new URL[] { archiveUrl });
			Applet app = (Applet) localURLClassLoader.loadClass(className).newInstance();
			app.setStub(new WebAppletStub(app, baseUrl, (HashMap<String, String>) configuration.get("param")));
			app.setPreferredSize(dimension);
			app.setVisible(true);
			app.init();
			app.start();
			return app;
		} catch(Exception e) {
			throw new AppletLoaderException(e);
		}
	}

	/**
	 * Get the configuration used in this loader
	 * @return The configuration
	 */
	public JagexConfiguration getConfiguration() {
		return configuration;
	}
}
