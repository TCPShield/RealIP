package net.tcpshield.tcpshield.util.validation.timestamp;

import net.tcpshield.tcpshield.TCPShieldPacketHandler;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class TimestampValidatorTest {

	private static final long NOW = 1700000000L;

	private static final TCPShieldPlugin PLUGIN = new TCPShieldPlugin() {

		private final ConfigProvider configProvider = new ConfigProvider() {

			@Override
			protected void reset() {
			}

			@Override
			protected void load() {
			}

			@Override
			public void reload() {
			}

			@Override
			protected void checkNodes(String... nodes) {
			}

		};

		@Override
		public ConfigProvider getConfigProvider() {
			return configProvider;
		}

		@Override
		public Logger getLogger() {
			return Logger.getAnonymousLogger();
		}

		@Override
		public TCPShieldPacketHandler getPacketHandler() {
			return null;
		}

		@Override
		public Debugger getDebugger() {
			return null;
		}

	};

	private static TimestampValidator validatorAt(long now) {
		return new TimestampValidator(PLUGIN) {
			@Override
			public long getUnixTime() {
				return now;
			}
		};
	}

	@Test
	public void acceptsTimestampsWithinTheAllowedDifference() {
		TimestampValidator validator = validatorAt(NOW);

		assertTrue(validator.validate(NOW));
		assertTrue(validator.validate(NOW + 3));
		assertTrue(validator.validate(NOW - 3));
	}

	@Test
	public void rejectsTimestampsOutsideTheAllowedDifference() {
		TimestampValidator validator = validatorAt(NOW);

		assertFalse(validator.validate(NOW + 4));
		assertFalse(validator.validate(NOW - 4));
		assertFalse(validator.validate(0));
		assertFalse(validator.validate(Long.MAX_VALUE));
		assertFalse(validator.validate(Long.MIN_VALUE));
	}

	@Test
	public void handlesTimestampsBeyondTheIntegerRange() {
		TimestampValidator validator = validatorAt(3000000000L);

		assertTrue(validator.validate(3000000001L));
		assertFalse(validator.validate(2999999990L));
	}

	@Test
	public void emptyValidatorAcceptsEverything() {
		TimestampValidator validator = TimestampValidator.createEmpty(PLUGIN);

		assertTrue(validator.validate(0));
		assertTrue(validator.validate(Long.MAX_VALUE));
	}

	@Test
	public void defaultValidatorUsesTheSystemClock() {
		TimestampValidator validator = TimestampValidator.createDefault(PLUGIN);

		assertTrue(validator.validate(System.currentTimeMillis() / 1000));
		assertFalse(validator.validate(System.currentTimeMillis() / 1000 - 60));
	}

}
