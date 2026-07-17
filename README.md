# TCPShield RealIP — Luxorium Folia fork

This is Luxorium's fork of [TCPShield/RealIP](https://github.com/TCPShield/RealIP), updated for Paper and Folia 26.1.2. It validates TCPShield handshake payloads, rejects unauthorized direct connections when configured to do so, and replaces the proxy address with the player's real address.

## Compatibility

- Folia 26.1.2 and Paper 26.1.2
- Java 25 or newer
- BungeeCord and Velocity entry points are retained from upstream
- Floodgate support remains optional
- ProtocolLib remains an optional fallback for non-Folia Bukkit servers

Folia always uses Paper's native `PlayerHandshakeEvent`. The handler does not access entities, chunks, worlds, or Bukkit schedulers, so it does not cross region ownership boundaries. See [the Folia compatibility notes](docs/FOLIA.md) for details.

## Installation

1. Build the plugin or obtain the fork's release JAR.
2. Put the JAR on the public frontend that receives TCPShield connections.
3. Start the server once and review `plugins/TCPShield/config.yml`.
4. Keep `only-allow-proxy-connections: true` in production.
5. Firewall the backend so the Minecraft port accepts traffic only from TCPShield's published networks.

Do not install the plugin on both a proxy and its downstream game server. Install it only on the frontend that receives TCPShield's handshake.

## Building

The repository includes a pinned Gradle wrapper. Keep Gradle's cache inside the repository when building in a sandbox:

```bash
GRADLE_USER_HOME=.gradle-user-home ./gradlew clean build
```

The reproducible JAR is written to `build/libs/TCPShield-Folia-2.9.0-folia.1.jar`.

## Upstream service documentation

Follow TCPShield's [plugin setup guide](https://docs.tcpshield.com/panel/tcpshield-plugin) and [backend setup checklist](https://docs.tcpshield.com/troubleshooting/setup-checklist). This fork is maintained by Luxorium and is not an official TCPShield release.

## License

The upstream project and this fork are available under the MIT License. See [LICENSE](LICENSE).
