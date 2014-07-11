package org.rslite.jagex;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	public WorldInfo[] parse() throws IOException {
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

			ArrayList<WorldInfo> list = new ArrayList<>(worlds);

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
					return o1.worldId < o2.worldId ? -1 : 1;
				}
			});

			return list.toArray(new WorldInfo[list.size()]);
		}
	}

	/**
	 * A class representing a WorldList World
	 *
	 * @author Nikki
	 */
	public class WorldInfo {
		private int worldId;
		private int flags;
		
		private boolean members;
		private boolean pvp;
		private boolean highRisk;
		
		private String host;
		private String activity;
		private Location location;
		private int playerCount;
		
		public WorldInfo(int worldId, String host, String activity, int playerCount, Location location, boolean members, boolean pvp, boolean highRisk) {
			this.worldId = worldId;
			this.host = host;
			this.activity = activity;
			this.playerCount = playerCount;
			this.location = location;
			this.members = members;
			this.pvp = pvp;
			this.highRisk = highRisk;
		}
		
		public int getWorldId() {
			return worldId;
		}

		public int getFlags() {
			return flags;
		}

		public boolean isMembers() {
			return members;
		}

		public boolean isPvp() {
			return pvp;
		}

		public boolean isHighRisk() {
			return highRisk;
		}

		public String getHost() {
			return host;
		}

		public String getActivity() {
			return activity;
		}

		public Location getLocation() {
			return location;
		}

		public int getPlayerCount() {
			return playerCount;
		}

		@Override
		public String toString() {
			return "World [worldId=" + worldId + ", host=" + host + ", activity=" + activity + ", playerCount=" + playerCount + ", location=" + location + ", members=" + members + ", pvp=" + pvp + ", highRisk=" + highRisk + "]";
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

	public enum Location {
		UNITED_STATES, UNITED_KINGDOM, CANADA, AUSTRALIA, NETHERLANDS, SWEDEN, FINLAND, GERMANY
	}
}