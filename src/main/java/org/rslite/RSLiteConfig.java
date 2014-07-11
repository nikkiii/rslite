package org.rslite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A very messy and pretty bad way of doing a configuration.
 * <p/>
 * It allows for easy use from all classes without passing instances around.
 *
 * @author Nikki
 */
public class RSLiteConfig {

	/**
	 * The static properties object
	 */
	private static final Properties properties = new Properties();

	static {
		load();
	}

	/**
	 * Load the configuration from the basic file
	 */
	public static void load() {
		File file = new File(System.getProperty("user.home"), ".rslite");

		if (file.exists()) {
			try (InputStream input = new FileInputStream(file)) {
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save the configuration to a file
	 */
	public static void save() {
		File file = new File(System.getProperty("user.home"), ".rslite");

		try (OutputStream output = new FileOutputStream(file)) {
			properties.store(output, "RSLite Settings");
		} catch (IOException e) {
			// Eat the exception, not the best idea but too lazy to do better.
		}
	}

	/**
	 * Check if the backing property object has the specified key
	 *
	 * @param key The key to check
	 */
	public static boolean hasProperty(String key) {
		return properties.containsKey(key);
	}

	/**
	 * Get the property specified by the key from the properties object
	 *
	 * @param key The key to get
	 * @return the value
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Get the property specified by the key from the properties object, but as an integer
	 *
	 * @param key The key to get
	 * @return the value
	 */
	public static int getPropertyInt(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

	/**
	 * Set a value and save the properties to file.
	 *
	 * @param key   The key to set
	 * @param value The value to set
	 */
	public static void setProperty(String key, Object value) {
		properties.put(key, value.toString());
		save();
	}
}
