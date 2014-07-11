package org.rslite.jagex;

import java.util.HashMap;
import java.util.Map;

/**
 * A basic Configuration object specifically made for Jagex' Java configuration files
 *
 * @author Nikki
 */
public class JagexConfiguration {
	/**
	 * The backing data map
	 */
	private Map<String, Object> data = new HashMap<>();

	/**
	 * Put a simple value into the data map
	 * @param key The key to set
	 * @param value The value to set
	 */
	public void put(String key, Object value) {
		data.put(key, value);
	}

	/**
	 * Put a compound data value into the data map (usually msg or param)
	 *
	 * @param structure The master key
	 * @param key The sub key
	 * @param value The value
	 */
	public void putCompound(String structure, String key, Object value) {
		Map<String, Object> m = (Map<String, Object>) data.get(structure);
		if (m == null) {
			put(structure, m = new HashMap<>());
		}
		m.put(key, value);
	}

	/**
	 * Get a value (WARNING: UNCHECKED CAST, be careful as this is almost always a String!)
	 *
	 * @param key The key to get
	 * @param <T> The return type
	 * @return The value
	 */
	public <T> T get(String key) {
		if (key.contains(".")) {
			Map<String, Object> m = data;
			String[] sub = key.split("\\.");
			for (int i = 0; i < sub.length - 1; i++) {
				m = (Map<String, Object>) m.get(sub[i]);
			}
			return (T) m.get(sub[sub.length - 1]);
		}
		return (T) data.get(key);
	}

	/**
	 * Get the HashMap behind a compound map
	 *
	 * @param key The map name
	 * @return The Map instance
	 */
	public Map<String, Object> getCompoundMapping(String key) {
		return (Map<String, Object>) data.get(key);
	}

	/**
	 * Check if this configuration has a value, splitting on "." for compound values
	 *
	 * @param key The key to check
	 * @return Whether the configuration has the specified key or not
	 */
	public boolean has(String key) {
		if (key.contains(".")) {
			Map<String, Object> m = data;
			String[] sub = key.split("\\.");
			for (int i = 0; i < sub.length - 1; i++) {
				m = (Map<String, Object>) m.get(sub[i]);
			}
			return m.containsKey(sub[sub.length - 1]);
		}
		return data.containsKey(key);
	}
}
