# TCPShield
TCPShield is the plugin for the same named DDoS mitigation service [TCPShield](https://tcpshield.com).

This plugin is responsible for validating clients join via the TCPShield network.
It also parses passed IP addresses so the server is aware of the real player IP address.  

### Compatibility

TCPShield is compatible with Spigot / CraftBukkit, BungeeCord and Velocity.

When using Spigot / CraftBukkit, [ProtocolLib](https://github.com/aadnk/ProtocolLib) needs to be installed.

### Setup
Setting up the plugin is easy as pie. Please follow [these](https://docs.tcpshield.com/panel/tcpshield-plugin) guidelines. 

### Notes

* Folia is supported
* Whitelist files go in `ip-whitelist` inside the plugin's data folder
* Whitelist entries are one per line, either an IPv4 or IPv6 address or CIDR range, such as `203.0.113.7` or `2001:db8::/32`
* Blank or invalid whitelist lines are skipped
* Every file in the folder is read at startup, so changes need a restart
* Whitelisted connections without TCPShield data pass through untouched. Other direct connections are refused unless `only-allow-proxy-connections` is false, in which case their addresses stay unchanged
* On Paper 1.20.5 and 1.20.6, `prefer-protocollib` with ProtocolLib 5.2.1 or older logs an error and falls back to the Paper handler. Spigot / CraftBukkit always uses ProtocolLib
* A connection has always been refused when the server clock is more than 3 seconds away from the time TCPShield signed into it, which is why a wrong server clock refuses players. A refused handshake is only logged with `debug-mode` on
* `htpdate` mode asks google.com for the time once at startup, giving up after 10 seconds, and uses that time or falls back to the server clock if google.com cannot be reached. `off` skips the time check but the signature is still verified
* Bedrock players' IPv6 addresses are handled through Geyser and Floodgate. `enable-geyser-support` only has an effect when Floodgate is installed
* If an option is missing from `config.yml`, the plugin logs a warning and replaces the whole file with the default one

### Compiling
In order to compile TCPShield, [install Gradle](https://docs.gradle.org/current/userguide/installation.html) and run the following command in the project folder:
```
gradle build
```

The dependencies should install themselves automatically. After the build has finished, the compiled jar file can be found under `/build/libs`.

### Support
See [Contact](https://tcpshield.com/#contact)

### Contributors

These wonderful contributors have helped TCPShield make this plugin better! 

* [Dylan Keir](https://github.com/DylanKeir)
* [Paul Zhang](https://github.com/paulzhng)
* [RyanDeLap](https://github.com/RyanDeLap)
* [PlumpOrange](https://github.com/xPlumpOrange/)
