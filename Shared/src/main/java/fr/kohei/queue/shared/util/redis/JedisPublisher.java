package fr.kohei.queue.shared.util.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

/**
 * @author TehNeon
 * @since 8/29/2017
 * <p>
 * A simple way to create a publisher to push/write/publish messages onto the targeted channel.
 */
@RequiredArgsConstructor
public class JedisPublisher<K> {
	@Getter private final JedisSettings jedisSettings;
	private final String channel;

	/**
	 * Sends the required message to the channel that we are currently on.
	 */
	public void write(K message) {
		Jedis jedis = null;
		try {
			jedis = this.jedisSettings.getJedisPool().getResource();
			jedis.publish(this.channel, message.toString());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
