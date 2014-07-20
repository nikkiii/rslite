package org.rslite.jagex.world;

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