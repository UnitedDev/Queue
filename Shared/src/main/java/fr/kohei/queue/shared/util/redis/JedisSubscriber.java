package fr.kohei.queue.shared.util.redis;

import fr.kohei.queue.shared.util.redis.subscription.JedisSubscriptionGenerator;
import fr.kohei.queue.shared.util.redis.subscription.JedisSubscriptionHandler;
import fr.kohei.queue.shared.util.redis.subscription.impl.JsonJedisSubscriptionGenerator;
import fr.kohei.queue.shared.util.redis.subscription.impl.StringJedisSubscriptionGenerator;
import com.google.gson.JsonObject;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author TehNeon
 * @since 8/29/2017
 * <p>
 * A simple way to create a subscriber onto a Redis channel to listen to messages. This will not listen to messages that
 * are deemed as urgent.
 */
public class JedisSubscriber<K> {

	private static final Map<Class, JedisSubscriptionGenerator> GENERATORS = new HashMap<>();

	static {
		GENERATORS.put(String.class, new StringJedisSubscriptionGenerator());
		GENERATORS.put(JsonObject.class, new JsonJedisSubscriptionGenerator());
	}

	protected final String channel;
	private final Class<K> typeParameter;
	private final JedisSettings jedisSettings;
	private final Jedis jedis;
	@Getter
	private JedisPubSub pubSub;

	private JedisSubscriptionHandler<K> jedisSubscriptionHandler;

	/**
	 * Requires the {@link JedisSettings} and a channel to listen to.
	 */
	public JedisSubscriber(JedisSettings jedisSettings, String channel, Class<K> typeParameter,
	                       JedisSubscriptionHandler<K> jedisSubscriptionHandler) {
		this.jedisSettings = jedisSettings;
		this.channel = channel;
		this.typeParameter = typeParameter;
		this.jedisSubscriptionHandler = jedisSubscriptionHandler;

		this.pubSub = new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				JedisSubscriptionGenerator<K> jedisSubscriptionGenerator = GENERATORS.get(typeParameter);

				if (jedisSubscriptionGenerator != null) {
					K object = jedisSubscriptionGenerator.generateSubscription(message);
					JedisSubscriber.this.jedisSubscriptionHandler.handleMessage(object);
				} else {
					System.out.println("Generator type is null");
				}
			}
		};

		this.jedis = new Jedis(this.jedisSettings.getAddress(), this.jedisSettings.getPort());
		this.authenticate();
		this.connect();
	}

	/**
	 * Checks the {@link JedisSettings} if there is a password, and if there is it will authenticate with the password
	 * that is given.
	 */
	private void authenticate() {
		if (this.jedisSettings.hasPassword()) {
			this.jedis.auth(this.jedisSettings.getPassword());
		}
	}

	/**
	 * Creates the thread for the {@link JedisPubSub} to be subscribed to on the channel which is targeted.
	 */
	private void connect() {
		Logger.getGlobal().info("Jedis is now reading on " + this.channel);
		new Thread(() -> {
			try {
				this.jedis.subscribe(this.pubSub, this.channel);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.getGlobal().info("For some odd reason our JedisSubscriber(" + this.channel + ") threw an " +
				                        "exception");
				close();
				connect();
			}
		}).start();
	}

	/**
	 * Closes the {@link JedisPubSub} connection and it will close the {@link Jedis} connection
	 */
	public void close() {
		Logger.getGlobal().info("Jedis is no longer reading on " + this.channel);

		if (this.pubSub != null) {
			this.pubSub.unsubscribe();
		}

		if (this.jedis != null) {
			this.jedis.close();
		}
	}

}
