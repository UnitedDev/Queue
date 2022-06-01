package fr.kohei.queue.shared.util.redis;

import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author TehNeon
 * @since 8/29/2017
 * <p>
 * Used to store all the Redis login data for an instance of {@link JedisListener}, {@link JedisPublisher}, or {@link
 * JedisSubscriber}.
 */
@Getter
public class JedisSettings {

	private final String address;
	private final int port;
	private final String password;
	private final JedisPool jedisPool;

	/**
	 * Creates settings with the IP address, the port, and the password to the redis server that is being targeted. Will use a standard config
	 *
	 * @param address
	 * @param port
	 * @param password
	 */
	public JedisSettings(String address, int port, String password) {
		this(address, port, password, new JedisPoolConfig());
	}

	/**
	 * Creates settings with the IP address, the port, the password, and optional pool settings.
	 *
	 * @param address
	 * @param port
	 * @param password
	 */
	public JedisSettings(String address, int port, String password, JedisPoolConfig config) {
		this.address = address;
		this.port = port;
		this.password = password;

		this.jedisPool = new JedisPool(config, this.address, this.port, 0, this.password);
	}

	/**
	 * Creates settings with an IP address and a password. It will use the default redis port.
	 *
	 * @param address
	 * @param password
	 */
	public JedisSettings(String address, String password) {
        this(address, 6379, password);
    }

	/**
	 * Creates settings with an IP address. It will use the default redis port, and use no password.
	 *
	 * @param address
	 */
	public JedisSettings(String address) {
		this(address, null);
	}

	/**
	 * Does this {@link JedisSettings} have a password set to it, and if so should the Jedis instance that is used actually
	 * authenticate.
	 *
	 * @return
	 */
	public boolean hasPassword() {
		return this.password != null;
	}

}
