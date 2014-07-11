package org.rslite.loader;

/**
 * @author Nikki
 */
public class LoaderConfigs {
	/**
	 * The Oldschool Runescape loader configuration
	 */
	public static final LoaderConfig RUNESCAPE_OLDSCHOOL = new LoaderConfig("OldSchool Runescape", "http://oldschool%d.runescape.com/jav_config.ws", "http://oldschool.runescape.com/jav_config.ws", false);

	/**
	 * The Runescape 3 loader configuration
	 */
	public static final LoaderConfig RUNESCAPE_3 = new LoaderConfig("Runescape 3", "http://world%d.runescape.com/jav_config.ws", "http://runescape.com/jav_config.ws", true);
}
