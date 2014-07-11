package org.rslite.jagex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A parser for Jagex' jav_config.ws files
 *
 * @author Nikki
 */
public class JagexConfigurationParser {
	/**
	 * The configuration file url
	 */
	private final URL configUrl;

	public JagexConfigurationParser(URL configUrl) {
		this.configUrl = configUrl;
	}

	/**
	 * Parse the configuration file
	 * @return The parsed JagexConfiguration object
	 * @throws IOException If an IO error occurred, usually connection related
	 */
	public JagexConfiguration parse() throws IOException {
		JagexConfiguration configuration = new JagexConfiguration();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(configUrl.openStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() < 1) continue;

				String key = line.substring(0, line.indexOf('='));
				String value = line.substring(line.indexOf('=')+1);

				switch(key) {
					case "msg":
					case "param":
						configuration.putCompound(key, value.substring(0, value.indexOf('=')), value.substring(value.indexOf('=')+1));
						break;
					default:
						configuration.put(key, value);
				}
			}
		}
		return configuration;
	}
}
