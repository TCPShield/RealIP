# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project uses Semantic Versioning for fork releases.

## [Unreleased]

### Changed (2.9.0-folia.3)

- Target Folia/Paper 26.2: compile against `paper-api:26.2.build.92-stable` and declare `api-version: '26.2'`.
- Require JDK 25 rather than JDK 26 to build, matching the Java 25 runtime baseline the plugin targets.

### Performance (2.9.0-folia.3)

- Resolve the source address only for unrecognised payloads. It was previously resolved for every connection despite being used solely by the allowlist branch, putting a potentially blocking name lookup on the Netty event loop.
- Precompile the `///` payload separator; `String.split` was compiling a fresh `Pattern` per connection.
- Drop the unbounded allowlist memoisation cache. It allocated a string per lookup and grew without bound under attacker-chosen source addresses, to avoid a few byte comparisons.
- Stop capturing stack traces on handshake-rejection exceptions, which an attacker could otherwise trigger once per connection.

### Fixed (2.9.0-folia.3)

- Give the `htpdate` synchronisation request connect and read timeouts and close its socket. It previously leaked the socket and could pin a thread indefinitely against an unresponsive peer.
- Run `htpdate` synchronisation on a dedicated daemon thread instead of the common `ForkJoinPool`, and log failures instead of throwing into a task whose exception was never observed.

### Added

- Explicit Folia 26.1.2 support and deployment documentation.
- Gradle 9.6.1 wrapper and reproducible JAR output.
- IPv4, IPv6, single-host, and invalid-mask CIDR regression tests.

### Changed

- Compile against Paper API `26.1.2.build.74-stable` and emit Java 25 bytecode.
- Always select Paper's native handshake event on Folia, ignoring the ProtocolLib preference there.
- Make the concurrent handshake IP allowlist cache thread-safe.
- Accept bare IPv4 and IPv6 addresses as single-host allowlist entries and reject invalid masks cleanly.
- Fail with a descriptive CIDR initialization error when the whitelist directory cannot be created or listed.
- Use current Paper Adventure disconnect messages and current BungeeCord socket-address accessors.
- Resolve ProtocolLib, BungeeCord, Velocity, and Floodgate APIs from Maven instead of requiring a local JAR.

[Unreleased]: https://github.com/Luxorium/RealIP/compare/2.8.1...HEAD
