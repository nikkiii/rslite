package org.rslite.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple ping/latency checking class for Java,
 * Using the "ping" command or if absolutely necessary, a socket with the port.
 *
 * @author Nikki
 */
public class LatencyChecker {

	/**
	 * This SHOULD match any time results (windows is time=<time>ms, linux is time=<time> ms)
	 */
	private static final Pattern RESULT_PATTERN = Pattern.compile("([0-9\\.]+)\\s?ms");

	/**
	 * Check the latency of a specified host, falling back to a socket with port if needed.
	 *
	 * @param host The host to check.
	 * @param port The port to check.
	 * @return The latency, in milliseconds
	 * @throws IOException If an error occurred while testing
	 */
	public static long checkLatency(String host, int port) throws IOException {
		OperatingSystem os = getPlatform();

		String output = "";

		try {
			switch (os) {
				case WINDOWS:
					output = executeProcess("ping", "-n", "1", host);
					break;
				default:
					output = executeProcess("ping", "-c", "1", host);
					break;
			}
		} catch (IOException e) {
			// Silently fail and fallback to sockets
		}

		Matcher m = RESULT_PATTERN.matcher(output);
		if (m.find()) {
			return (long) Double.parseDouble(m.group(1));
		}

		long nanoStart = System.nanoTime();
		try (Socket socket = new Socket(host, port)) {
			// Nothing
		}
		long nanoTaken = nanoStart - System.nanoTime();

		return nanoTaken / 1000000;
	}

	/**
	 * Execute the specified process and return the output
	 * @param args The process arguments
	 * @return The process output
	 * @throws IOException If an error occurred while executing
	 */
	private static String executeProcess(String... args) throws IOException {
		Process process = Runtime.getRuntime().exec(args);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			StringBuilder out = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line).append('\n');
			}

			return out.toString();
		}
	}

	/**
	 * An enum containing operating system types
	 *
	 * @author Nikki
	 *
	 */
	public static enum OperatingSystem {
		LINUX, SOLARIS, WINDOWS, MAC, UNKNOWN
	}

	/**
	 * Get the current platform
	 *
	 * @return The current platform
	 */
	public static OperatingSystem getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
			return OperatingSystem.WINDOWS;
		if (osName.contains("mac"))
			return OperatingSystem.MAC;
		if (osName.contains("solaris"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("sunos"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("linux"))
			return OperatingSystem.LINUX;
		if (osName.contains("unix"))
			return OperatingSystem.LINUX;
		return OperatingSystem.UNKNOWN;
	}
}
