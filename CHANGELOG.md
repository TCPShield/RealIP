# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project uses Semantic Versioning for fork releases.

## [Unreleased]

### Added

- Explicit Folia 26.1.2 support and deployment documentation.
- Gradle 9.6.1 wrapper and reproducible JAR output.
- IPv4, IPv6, single-host, and invalid-mask CIDR regression tests.

### Changed

- Compile against Paper API `26.1.2.build.74-stable` and emit Java 25 bytecode.
- Always select Paper's native handshake event on Folia, ignoring the ProtocolLib preference there.
- Make the concurrent handshake IP allowlist cache thread-safe.
- Accept bare IPv4 and IPv6 addresses as single-host allowlist entries and reject invalid masks cleanly.
- Use current Paper Adventure disconnect messages and current BungeeCord socket-address accessors.
- Resolve ProtocolLib, BungeeCord, Velocity, and Floodgate APIs from Maven instead of requiring a local JAR.

[Unreleased]: https://github.com/Luxorium/RealIP/compare/2.8.1...HEAD
