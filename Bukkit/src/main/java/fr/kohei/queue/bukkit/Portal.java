package fr.kohei.queue.bukkit;

import fr.kohei.BukkitAPI;
import fr.kohei.queue.bukkit.command.commands.*;
import fr.kohei.queue.bukkit.config.FileConfig;
import fr.kohei.queue.bukkit.config.Language;
import fr.kohei.queue.bukkit.jedis.PortalSubscriptionHandler;
import fr.kohei.queue.bukkit.server.Server;
import fr.kohei.queue.bukkit.thread.ReminderThread;
import fr.kohei.queue.bukkit.thread.UpdateThread;
import fr.kohei.queue.bukkit.util.redis.JedisConfig;
import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.util.redis.JedisPublisher;
import fr.kohei.queue.shared.util.redis.JedisSubscriber;
import com.google.gson.JsonObject;
import fr.kohei.queue.bukkit.listener.PlayerListener;
import fr.kohei.queue.bukkit.priority.PriorityProvider;
import fr.kohei.queue.bukkit.priority.impl.DefaultPriorityProvider;
import fr.kohei.queue.shared.jedis.JedisChannel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;

@Getter
public class Portal extends JavaPlugin {

    @Getter
    private static Portal instance;

    private FileConfig mainConfig;

    private Language language;

    private Server portalServer;

    private JedisConfig jedisConfig;
    private JedisPublisher bukkitPublisher, independentPublisher;
    private JedisSubscriber subscriber;

    @Setter
    private PriorityProvider priorityProvider;

    @Override
    public void onEnable() {
        instance = this;

        this.mainConfig = new FileConfig(this, "config.yml");

        this.language = new Language();
        this.language.load();
        this.jedisConfig = new JedisConfig(this);

        this.portalServer = new Server(
                (BukkitAPI.getFactory(Bukkit.getPort()).getName().contains("Lobby") ? "Lobby-" : "UHC-") + Bukkit.getPort(),
                BukkitAPI.getFactory(Bukkit.getPort()).getName().contains("Lobby")
        );

        this.bukkitPublisher = new JedisPublisher(getJedisConfig().toJedisSettings(), JedisChannel.BUKKIT);
        this.independentPublisher = new JedisPublisher(getJedisConfig().toJedisSettings(), JedisChannel.INDEPENDENT);
        this.subscriber = new JedisSubscriber(getJedisConfig().toJedisSettings(), JedisChannel.BUKKIT, JsonObject.class, new PortalSubscriptionHandler());

        this.priorityProvider = new DefaultPriorityProvider();

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            JsonObject object = Portal.getInstance().getPortalServer().getServerData();
            object.addProperty("action", JedisAction.ADD_SERVER.name());
            Portal.getInstance().getIndependentPublisher().write(object);
        }, 100L);

        // Start threads
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new UpdateThread(), 10L, 1L, java.util.concurrent.TimeUnit.SECONDS);
        new ReminderThread().start();

        // Register commands
        new JoinQueueCommand();
        new LeaveQueueCommand();
        new ForceSendCommand();
        new DataDumpCommand();
        new QueueToggleCommand();
        new QueueClearCommand();

        // Register listeners
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // Register plugin message channels
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        JsonObject object = Portal.getInstance().getPortalServer().getServerData();
        object.addProperty("action", JedisAction.REMOVE_SERVER.name());
        Portal.getInstance().getIndependentPublisher().write(object);
    }
}
