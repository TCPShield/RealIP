package net.tcpshield.tcpshield.geyser;

import net.tcpshield.tcpshield.TCPShieldPlugin;
import org.geysermc.floodgate.api.InstanceHolder;

import java.util.Arrays;

public class GeyserHandshakeHandler {

	private final TCPShieldPlugin plugin;

	public GeyserHandshakeHandler(TCPShieldPlugin plugin) {
		this.plugin = plugin;
	}

	public void init() {
		InstanceHolder.getHandshakeHandlers().addHandshakeHandler(data -> {
			if (data.getIp() == null) {
				// not bedrock
				this.plugin.getDebugger().warn("Connection with no bedrock data joined and ignored: username = %s, hostname = %s", data.getCorrectUsername(), data.getHostname());
				return;
			}

			this.plugin.getDebugger().warn("Bedrock connection joined and validated: username = %s, hostname = %s", data.getCorrectUsername(), data.getHostname());

			String oldPayload = data.getHostname();
			if (oldPayload.contains("///")) {
				oldPayload = oldPayload.split("///")[0];
			}

			String hostname = oldPayload;
			String realIp = data.getIp();
			String timestamp = "0";
			String signature = GeyserUtils.SESSION_SECRET;

			// so floodgate likes to append "\0Floodgate\0" or something stupid to the hostname
			// which just completely breaks this, so we have to pass these in a different
			// order to stop bungeecord detecting it as FML data, thanks floodgate.
			String newHostname = realIp + ":0///" + signature + "///" + timestamp + "///" + hostname;
			if (hostname.contains("\0")) { // this is usually fine because FML, providing the hostname is first
				this.plugin.getDebugger().warn("Hostname contains null byte: " + Arrays.toString(hostname.toCharArray()));
			}

			this.plugin.getDebugger().warn("Setting hostname to %s - %s", newHostname, Arrays.toString(newHostname.toCharArray()));
			data.setHostname(newHostname);
		});
	}

}
