package net.tcpshield.tcpshield.util.validation.timestamp.impl;

import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.validation.timestamp.TimestampValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * A timestamp validator that offsets system time with Google's to sync
 * tiemstamps
 */
public class HTPDateTimestampValidator extends TimestampValidator {

	private static final int CONNECT_TIMEOUT_MILLIS = 5000;
	private static final int READ_TIMEOUT_MILLIS = 5000;

	private volatile long htpDateOffset = 0;

	public HTPDateTimestampValidator(TCPShieldPlugin plugin) {
		super(plugin);

		// A dedicated daemon thread rather than the common ForkJoinPool: this is
		// blocking network I/O, and the common pool is shared with every parallel
		// stream on the server. Failures leave the offset at 0, which degrades to
		// plain system time instead of rejecting connections.
		Thread worker = new Thread(() -> {
			try {
				updateHTPDateOffset();
			} catch (Exception e) {
				plugin.getLogger().warning("htpdate synchronisation failed; falling back to system time for timestamp validation. "
						+ "Set timestamp-validation to 'system' to silence this. Cause: " + e);
				plugin.getDebugger().exception(e);
			}
		}, "TCPShield-htpdate");
		worker.setDaemon(true);
		worker.start();
	}


	private void updateHTPDateOffset() throws IOException {
		// Timeouts are mandatory here: without them an unreachable or silent peer
		// pins this thread and its socket for the lifetime of the server.
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress("google.com", 80), CONNECT_TIMEOUT_MILLIS);
			socket.setSoTimeout(READ_TIMEOUT_MILLIS);

			String payload = "HEAD http://google.com/ HTTP/1.1\r\nHost: google.com\r\nUser-Agent: tcpshield/1.0\r\nPragma: no-cache\r\nCache-Control: no-cache\r\nConnection: close\r\n\r\n";
			socket.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));

			long readTime = System.currentTimeMillis(); // assuming server -> client time is negligible

			List<String> response = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.toList());

			Date serverDate = parseDate(response);

			htpDateOffset = Math.round((serverDate.getTime() - readTime) / 1000.0) * 1000; // the HTTP protocol only returns time in seconds; round offset
		}
	}

	private Date parseDate(List<String> response) {
		for (String line : response) {
			if (!line.startsWith("Date: "))
				continue;

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
			try {
				return sdf.parse(line.substring("Date: ".length()));
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
		}

		throw new IllegalArgumentException("no date line found - response: " + response);
	}

	@Override
	public long getUnixTime() {
		return (System.currentTimeMillis() + htpDateOffset) / 1000;
	}

}
