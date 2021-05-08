package net.tcpshield.tcpshield.util.validation.cidr;

import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.exception.phase.CIDRException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.*;

/**
 * A CIDR validator for TCPShield
 */
public class CIDRValidator {

	private final TCPShieldPlugin plugin;

	private final File ipWhitelistFolder;

	private final List<CIDRMatcher> cidrMatchers;
	private final Set<String> cache = new HashSet<>(); // Max potential size is equilivent to the amount of whitelisted IP's

	public CIDRValidator(TCPShieldPlugin plugin) throws CIDRException {
		this.plugin = plugin;

		ipWhitelistFolder = new File(plugin.getConfigProvider().getDataFolder(), "ip-whitelist");
		if (!ipWhitelistFolder.exists())
			ipWhitelistFolder.mkdir();

		try {
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

		return matchers;
	}

	private List<String> loadWhitelists() throws FileNotFoundException {
		List<String> whitelists = new ArrayList<>();

		for (File file : Objects.requireNonNull(ipWhitelistFolder.listFiles())) {
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
		String ip = inetAddress.getHostAddress();

		if (cache.contains(ip))
			return true;

		for (CIDRMatcher cidrMatcher : cidrMatchers)
			if (cidrMatcher.match(inetAddress)) {
				cache.add(ip);
				return true;
			}

		return false;
	}

}
