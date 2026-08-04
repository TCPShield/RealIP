# Folia 26.2 compatibility

## Native handshake path

On Folia, TCPShield always registers Paper's native `PlayerHandshakeEvent` listener. The `prefer-protocollib` setting is intentionally ignored and a warning is logged if it is enabled.

The event occurs during the connection handshake, before a Bukkit `Player` exists. The handler only:

- validates the TCPShield timestamp and signature;
- validates optional IP allowlist entries;
- rewrites the handshake hostname and socket address; and
- marks an invalid handshake as failed.

It does not access region-owned entities, chunks, blocks, or worlds. It does not use `BukkitScheduler`, `BukkitRunnable`, or `runTask*` methods.

## Concurrency

Multiple connections can be processed concurrently. The handler keeps no mutable shared state: the IP allowlist is frozen at startup and only read afterwards, and signature validation creates a new JCA `Signature` object per validation while sharing only the immutable public key.

The handshake runs on a Netty event-loop thread, so it must not block. Address resolution is therefore confined to the unrecognised-payload branch, where the allowlist is consulted; a valid TCPShield payload is authenticated by its signature and never triggers a name lookup.

The optional `htpdate` initialization performs its network request on a dedicated daemon thread with connect and read timeouts, and publishes its offset through a volatile field. If synchronization fails the offset stays at zero, degrading to plain system time rather than rejecting connections.

## Metadata and runtime baseline

The Bukkit descriptor declares:

```yaml
api-version: '26.2'
folia-supported: true
```

The project compiles against `paper-api:26.2.build.92-stable` and emits Java 25 bytecode.

## Deployment checks

After installation, confirm all of the following:

1. Folia logs that TCPShield enabled without an unsupported-plugin warning.
2. A connection through the protected hostname reports the player's real address.
3. A direct connection to the backend is rejected.
4. The host firewall permits the Minecraft backend port only from TCPShield's current address ranges.

Plugin validation is not a replacement for the host firewall. If the backend remains reachable from arbitrary source addresses, scanners can still identify and attack the origin network.
