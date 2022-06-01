package fr.kohei.queue.bukkit.util.redis;

import fr.kohei.queue.bukkit.util.Config;
import fr.kohei.queue.shared.util.redis.JedisSettings;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TehNeon
 * @since 8/29/2017
 */
public class JedisConfig extends Config {

	public JedisConfig(JavaPlugin plugin) {
		super("jedis", plugin);

		// If the file was just created, save a default config
		if (this.wasCreated) {
			this.getConfig().set("jedis.host", "127.0.0.1");
			this.getConfig().set("jedis.port", 6937);
			this.getConfig().set("jedis.password", null);
			this.save();
		}
	}

	public JedisSettings toJedisSettings() {
		return new JedisSettings(this.getConfig().getString("jedis.host"), this.getConfig().getInt("jedis.port"), this.getConfig().getString("jedis.password"));
	}
}
