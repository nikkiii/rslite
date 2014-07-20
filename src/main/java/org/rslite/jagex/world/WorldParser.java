package org.rslite.jagex.world;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Loads the Oldschool Runescape World List data
 *
 * @author Nikki
 */
public class WorldParser {

	/**
	 * Parse the world list
	 * @return The world info objecst
	 * @throws IOException If an error occurred while parsing
	 */
	public List<WorldInfo> parse() throws IOException {
		URLConnection conn = new URL("http://oldschool.runescape.com/slr").openConnection();
		
		try (InputStream input = conn.getInputStream()) {

			byte[] sizeBytes = new byte[4];
			input.read(sizeBytes, 0, sizeBytes.length);

			int size = ((sizeBytes[0] & 0xFF) << 24) + ((sizeBytes[1] & 0xFF) << 16) + ((sizeBytes[2] & 0xFF) << 8) + (sizeBytes[3] & 0xFF);

			int total = 0;

			ByteBuffer data = ByteBuffer.allocate(size);

			byte[] buffer = new byte[1024];

			while (total < size) {
				int read = input.read(buffer, 0, buffer.length);
				if (read == -1) {
					throw new EOFException("Expected " + size + ", got " + (total + read));
				}
				if (read > (size - total)) {
					read = (size - total);
				}
				data.put(buffer, 0, read);
				total += read;
			}

			data.flip();

			int worlds = data.getShort();

			List<WorldInfo> list = new ArrayList<>(worlds);

			for (int i = 0; i < worlds; i++) {
				int worldId = data.getShort() & 0xFFFF;

				int flags = data.getInt();

				boolean members = (flags & 1) != 0;
				boolean pvp = (flags & 4) != 0;
				boolean highrisk = (flags & 1024) != 0;

				String host = readNullString(data);
				String activity = readNullString(data);
				Location location = Location.values()[data.get() & 0xFF];
				int playerCount = data.getShort();

				list.add(new WorldInfo(worldId, host, activity, playerCount, location, members, pvp, highrisk));
			}

			Collections.sort(list, new Comparator<WorldInfo>() {
				@Override
				public int compare(WorldInfo o1, WorldInfo o2) {
					return o1.getWorldId() < o2.getWorldId() ? -1 : 1;
				}
			});

			return list;
		}
	}

	/**
	 * Reads a null terminated string from the specified ByteBuffer
	 * @param buf The ByteBuffer to read from
	 * @return The final string
	 */
	private static String readNullString(ByteBuffer buf) {
		StringBuilder bldr = new StringBuilder();
		int b;
		while ((b = buf.get()) != 0) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}
}