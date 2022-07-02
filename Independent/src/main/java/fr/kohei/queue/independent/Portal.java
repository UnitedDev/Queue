package fr.kohei.queue.independent;

import com.google.gson.JsonObject;
import fr.kohei.common.api.CommonAPI;
import fr.kohei.common.cache.server.impl.LobbyServer;
import fr.kohei.common.cache.server.impl.UHCServer;
import fr.kohei.queue.independent.file.Config;
import fr.kohei.queue.independent.jedis.PortalSubscriptionHandler;
import fr.kohei.queue.independent.log.Logger;
import fr.kohei.queue.independent.thread.BroadcastThread;
import fr.kohei.queue.independent.thread.QueueThread;
import fr.kohei.queue.shared.jedis.JedisChannel;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.queue.shared.util.redis.JedisPublisher;
import fr.kohei.queue.shared.util.redis.JedisSettings;
import fr.kohei.queue.shared.util.redis.JedisSubscriber;
import lombok.Getter;


@Getter
public class Portal {

    @Getter
    private static Portal instance;

    private final Config config;
    private final CommonAPI api;

    private final JedisSettings settings;
    private final JedisSubscriber subscriber;

    private final JedisPublisher bukkitPublisher;

    private Portal() {
        this.api = CommonAPI.create();
        this.config = new Config("127.0.0.1", 6379, null);

        for (UHCServer uhcServer : api.getServerCache().getUhcServers().values()) {
            Queue.getQueues().add(new Queue("UHC-" + uhcServer.getPort()));
        }

        for (LobbyServer lobbyServer : api.getServerCache().getLobbyServers().values()) {
            config.getHubs().add("Lobby-" + lobbyServer.getPort());
        }

        // Instantiate jedis settings (and pool)
        this.settings = config.getJedisSettings();

        // Instantiate jedis pubsub
        this.subscriber = new JedisSubscriber<>(settings, JedisChannel.INDEPENDENT, JsonObject.class, new PortalSubscriptionHandler());
        this.bukkitPublisher = new JedisPublisher<>(settings, JedisChannel.BUKKIT);

        // Start threads
        new QueueThread().start();
        new BroadcastThread().start();

        Logger.print("Portal is now running...");

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (Portal.getInstance().getSettings() != null && !Portal.getInstance().getSettings().getJedisPool().isClosed()) {
                Portal.getInstance().getSettings().getJedisPool().close();
            }
        }));
    }

    public static void main(String[] args) {
        instance = new Portal();
    }



}
