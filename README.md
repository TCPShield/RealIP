# TCPShield
TCPShield is the plugin for the same named DDoS mitigation service [TCPShield](https://tcpshield.com).

This plugin is responsible for validating clients join via the TCPShield network.
It also parses passed IP addresses so the server is aware of the real player IP address.  

### Compatibility

TCPShield is compatible with Spigot / CraftBukkit, BungeeCord and Velocity.

When using Spigot / CraftBukkit, [ProtocolLib](https://github.com/aadnk/ProtocolLib) needs to be installed.

### Setup
Setting up the plugin is easy as pie. Please follow [these](https://docs.tcpshield.com/panel/tcpshield-plugin) guidelines. 

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
