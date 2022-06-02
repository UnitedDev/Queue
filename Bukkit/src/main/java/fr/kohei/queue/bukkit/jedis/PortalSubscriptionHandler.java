package fr.kohei.queue.bukkit.jedis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.kohei.BukkitAPI;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.bukkit.util.BungeeUtil;
import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.queue.shared.queue.QueuePlayer;
import fr.kohei.queue.shared.queue.QueuePlayerComparator;
import fr.kohei.queue.shared.queue.QueueRank;
import fr.kohei.queue.shared.server.ServerData;
import fr.kohei.queue.shared.util.redis.subscription.JedisSubscriptionHandler;
import fr.kohei.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

public class PortalSubscriptionHandler implements JedisSubscriptionHandler<JsonObject> {

    @Override
    public void handleMessage(JsonObject json) {
        JedisAction action = JedisAction.valueOf(json.get("action").getAsString());

        switch (action) {
            case LIST: {
                if (!Portal.getInstance().getPortalServer().isHub()) {
                    return;
                }

                for (JsonElement e : json.get("servers").getAsJsonArray()) {
                    final JsonObject serverJson = e.getAsJsonObject();
                    final String name = serverJson.get("name").getAsString();
                    ServerData serverData = ServerData.getByName(name);

                    if (serverData == null) {
                        serverData = new ServerData(name);
                    }

                    serverData.setOnlinePlayers(serverJson.get("online-players").getAsInt());
                    serverData.setMaximumPlayers(serverJson.get("maximum-players").getAsInt());
                    serverData.setWhitelisted(serverJson.get("whitelisted").getAsBoolean());
                    serverData.setLastUpdate(System.currentTimeMillis());

                    JsonArray array = serverJson.get("wlplayers").getAsJsonArray();
                    List<UUID> ids = new ArrayList<>();
                    for (JsonElement s : array) {
                        UUID id = UUID.fromString(s.getAsString());
                        ids.add(id);
                    }
                    serverData.setWhitelistedPlayers(ids);
                }

                for (JsonElement e : json.get("queues").getAsJsonArray()) {
                    final JsonObject queueJson = e.getAsJsonObject();
                    final String name = queueJson.get("name").getAsString();
                    Queue queue = Queue.getByName(name);

                    if (queue == null) {
                        queue = new Queue(name);
                    }

                    PriorityQueue<QueuePlayer> players = new PriorityQueue<>(new QueuePlayerComparator());

                    for (JsonElement pe : queueJson.get("players").getAsJsonArray()) {
                        JsonObject player = pe.getAsJsonObject();
                        JsonObject rank = player.get("rank").getAsJsonObject();

                        QueueRank queueRank = new QueueRank();
                        queueRank.setName(rank.get("name").getAsString());
                        queueRank.setPriority(rank.get("priority").getAsInt());

                        QueuePlayer queuePlayer = new QueuePlayer();
                        queuePlayer.setUuid(UUID.fromString(player.get("uuid").getAsString()));
                        queuePlayer.setRank(queueRank);
                        queuePlayer.setInserted(player.get("inserted").getAsLong());

                        players.add(queuePlayer);
                    }

                    queue.setPlayers(players);
                    queue.setEnabled(queueJson.get("status").getAsBoolean());
                }
            }
            break;
            case ADDED_PLAYER: {
                Queue queue = Queue.getByName(json.get("queue").getAsString());

                if (queue == null) {
                    return;
                }

                JsonObject player = json.get("player").getAsJsonObject();
                JsonObject rank = player.get("rank").getAsJsonObject();

                QueueRank queueRank = new QueueRank();
                queueRank.setName(rank.get("name").getAsString());
                queueRank.setPriority(rank.get("priority").getAsInt());

                QueuePlayer queuePlayer = new QueuePlayer();
                queuePlayer.setUuid(UUID.fromString(player.get("uuid").getAsString()));
                queuePlayer.setRank(queueRank);
                queuePlayer.setInserted(player.get("inserted").getAsLong());

                queue.getPlayers().add(queuePlayer);

                Player bukkitPlayer = Portal.getInstance().getServer().getPlayer(queuePlayer.getUuid());

                if (bukkitPlayer != null) {
                    for (String message : Portal.getInstance().getLanguage().getAdded(bukkitPlayer, queue)) {
                        bukkitPlayer.sendMessage(message);
                    }
                }
            }
            break;
            case REMOVED_PLAYER: {
                Queue queue = Queue.getByName(json.get("queue").getAsString());

                if (queue == null) {
                    return;
                }

                UUID uuid = UUID.fromString(json.get("player").getAsJsonObject().get("uuid").getAsString());

                queue.getPlayers().removeIf(queuePlayer -> queuePlayer.getUuid().equals(uuid));

                Player bukkitPlayer = Portal.getInstance().getServer().getPlayer(uuid);

                if (bukkitPlayer != null) {
                    for (String message : Portal.getInstance().getLanguage().getRemoved(queue)) {
                        bukkitPlayer.sendMessage(message);
                    }
                }
            }
            break;
            case SEND_PLAYER_SERVER: {
                String server = json.get("server").getAsString();

                Player player;

                // Send player by username or uuid
                if (json.has("username")) {
                    player = Bukkit.getPlayer(json.get("username").getAsString());
                } else {
                    player = Bukkit.getPlayer(UUID.fromString(json.get("uuid").getAsString
                            ()));
                }

                if (player == null) {
                    return;
                }

                player.sendMessage(ChatUtil.prefix("&fRedirection vers le serveur &c" + server));

                BungeeUtil.sendToServer(player,
                        BukkitAPI.getFactory(Integer.parseInt(server.split("-")[1])).getName());
            }
            break;
            case MESSAGE_PLAYER: {
                Player player = Portal.getInstance().getServer().getPlayer(json.get("uuid").getAsString());

                if (player == null) {
                    return;
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', json.get("message").getAsString()));
            }
            break;
        }
    }

}
