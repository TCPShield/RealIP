package net.tcpshield.tcpshield.util.validation.cidr;

import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.exception.phase.CIDRException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.*;

/**
 * A CIDR validator for TCPShield
 */
public class CIDRValidator {

	private final TCPShieldPlugin plugin;

	private final File ipWhitelistFolder;

	// Effectively immutable after construction, so concurrent handshakes on Folia's
	// network threads can read it without synchronisation.
	private final List<CIDRMatcher> cidrMatchers;

	public CIDRValidator(TCPShieldPlugin plugin) throws CIDRException {
		this.plugin = plugin;

		ipWhitelistFolder = new File(plugin.getConfigProvider().getDataFolder(), "ip-whitelist");

		try {
			Files.createDirectories(ipWhitelistFolder.toPath());
			List<String> whitelists = loadWhitelists();
			cidrMatchers = loadCIDRMatchers(whitelists);
		} catch (Exception e) {
			throw new CIDRException(e);
		}
	}


	private List<CIDRMatcher> loadCIDRMatchers(List<String> whitelists) {
		List<CIDRMatcher> matchers = new ArrayList<>();

		for (String whitelist : whitelists)
			try {
				matchers.add(CIDRMatcher.create(whitelist));
			} catch (Exception e) {
				plugin.getDebugger().warn("Exception occured while creating CIDRMatcher for \"%s\". Ignoring it.", whitelist);
				plugin.getDebugger().exception(e);
			}

		// Frozen so the final field is safely published to every network thread.
		return List.copyOf(matchers);
	}

	private List<String> loadWhitelists() throws FileNotFoundException {
		List<String> whitelists = new ArrayList<>();
		File[] whitelistFiles = ipWhitelistFolder.listFiles();
		if (whitelistFiles == null) {
			throw new CIDRException(new IOException("Unable to list whitelist directory: " + ipWhitelistFolder));
		}

		for (File file : whitelistFiles) {
			if (file.isDirectory())
				continue;

			try (Scanner scanner = new Scanner(file)) {
				while (scanner.hasNextLine()) {
					String cidrEntry = scanner.nextLine();
					whitelists.add(cidrEntry);
				}
			}
		}

		return whitelists;
	}

	/**
	 * Validates an InetAddress with CIDR matchers
	 * @param inetAddress The InetAddress to validate
	 * @return Boolean stating if the InetAddress is validated with CIDR
	 */
	public boolean validate(InetAddress inetAddress) {
		// Matching is a handful of byte comparisons per entry. A memoisation cache
		// keyed on the source IP would cost a string allocation per call and grow
		// without bound under attacker-chosen addresses, so match directly.
		for (int i = 0, size = cidrMatchers.size(); i < size; i++)
			if (cidrMatchers.get(i).match(inetAddress))
				return true;

		return false;
	}

}
