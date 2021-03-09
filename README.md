# TCPShield
TCPShield is the plugin for the same named DDoS mitigation service [TCPShield](https://tcpshield.com).

This plugin is responsible for validating clients join via the TCPShield network.
It also parses passed IP addresses so the server is aware of the real player IP address.  

### Compatibility

TCPShield is compatible with Spigot / CraftBukkit, BungeeCord, Velocity and Fabric.

When using Spigot / CraftBukkit, [ProtocolLib](https://github.com/aadnk/ProtocolLib) needs to be installed.
This does not apply when using Paper 1.16 build #503 or higher. 

### Setup
Setting up the plugin is easy as pie. Please follow [these](https://docs.tcpshield.com/config/tcpshield-plugin) guidelines. 

### Compiling
In order to compile TCPShield, [install Gradle](https://docs.gradle.org/current/userguide/installation.html) and run the following command in the project folder:
```
gradle build
```

The dependencies should install themselves automatically. After the build has finished, the compiled jar file can be found under `/build/libs`.

### Support
See [Contact](https://docs.tcpshield.com/about-us)

### Contributors

These wonderful contributors have helped TCPShield make this plugin better! 

* [Paul Zhang](https://github.com/paulzhng)
* [Draylar](https://github.com/Draylar)
* [RyanDeLap](https://github.com/RyanDeLap)
