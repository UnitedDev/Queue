package fr.kohei.queue.shared.util.redis;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class JedisImplementation {

	protected final JedisSettings jedisSettings;

	protected void cleanup(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	protected Jedis getJedis() {
		return this.jedisSettings.getJedisPool().getResource();
	}

}
