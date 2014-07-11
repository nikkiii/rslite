package org.rslite.loader;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.util.HashMap;

/**
 * An AppletStub for loading the Runescape applet
 */
public class WebAppletStub implements AppletStub {

	/**
	 * The applet instance
	 */
	private Applet applet;

	/**
	 * The parameter list
	 */
	private HashMap<String, String> parameters;

	/**
	 * The code base url
	 */
	private URL baseUrl;

	public WebAppletStub(Applet applet, URL baseUrl, HashMap<String, String> parameters) {
		this.applet = applet;
		this.baseUrl = baseUrl;
		this.parameters = parameters;
	}

	@Override
	public boolean isActive() {
		return applet.isActive();
	}

	@Override
	public URL getDocumentBase() {
		return baseUrl;
	}

	@Override
	public URL getCodeBase() {
		return baseUrl;
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public AppletContext getAppletContext() {
		return null;
	}

	@Override
	public void appletResize(int width, int height) {
		
	}

}
