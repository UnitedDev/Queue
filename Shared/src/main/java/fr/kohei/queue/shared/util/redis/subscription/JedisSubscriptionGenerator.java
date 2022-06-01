package fr.kohei.queue.shared.util.redis.subscription;

/**
 * @since 2017-09-02
 */
public interface JedisSubscriptionGenerator<K> {

	K generateSubscription(String message);

}
