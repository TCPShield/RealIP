package net.tcpshield.tcpshield.geyser;

import net.tcpshield.tcpshield.TCPShieldPlugin;
import org.geysermc.floodgate.api.InstanceHolder;

public class GeyserHandshakeHandler {

	private final TCPShieldPlugin plugin;

	public GeyserHandshakeHandler(TCPShieldPlugin plugin) {
		this.plugin = plugin;
	}

	public void init() {
		InstanceHolder.getHandshakeHandlers().addHandshakeHandler(data -> {
			if (data.getBedrockData() == null) {
				return;
			}

			String hostname = data.getHostname();
			String realIp = data.getIp();
			String timestamp = "0";
			String signature = GeyserUtils.SESSION_SECRET;

			String newHostname = hostname + "///" + realIp + ":0///" + timestamp + "///" + signature;
			data.setHostname(newHostname);
		});
	}

}
