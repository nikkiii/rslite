package org.rslite.loader;

/**
 * A loader configuration
 * @author Nikki
 */
public class LoaderConfig {
	private final String name;
	private final String autoUrl;
	private final String url;
	private final boolean resizable;

	public LoaderConfig(String name, String url, String autoUrl, boolean resizable) {
		this.name = name;
		this.autoUrl = autoUrl;
		this.url = url;
		this.resizable = resizable;
	}

	public String getName() {
		return name;
	}

	public String getAutoUrl() {
		return autoUrl;
	}

	public String getUrl() {
		return url;
	}

	public String getUrlForWorld(int world) {
		return String.format(url, world);
	}

	public boolean isResizable() {
		return resizable;
	}

	@Override
	public String toString() {
		return name;
	}
}
