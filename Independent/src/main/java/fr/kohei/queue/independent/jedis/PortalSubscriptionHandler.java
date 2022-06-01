package fr.kohei.queue.independent.jedis;

import fr.kohei.common.api.CommonAPI;
import fr.kohei.queue.independent.Portal;
import fr.kohei.queue.independent.log.Logger;
import fr.kohei.queue.shared.util.redis.subscription.JedisSubscriptionHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.queue.shared.queue.QueuePlayer;
import fr.kohei.queue.shared.queue.QueueRank;
import fr.kohei.queue.shared.server.ServerData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PortalSubscriptionHandler implements JedisSubscriptionHandler<JsonObject> {

    public void handleMessage(JsonObject json) {
        JedisAction action = JedisAction.valueOf(json.get("action").getAsString());

        Logger.print("Received " + action.name());

        switch (action) {
            case UPDATE: {
                String name = json.get("name").getAsString();
                ServerData serverData = ServerData.getByName(name);
                Queue queue = Queue.getByName(name);

                if (serverData == null) {
                    // Enable queue for the first time
                    if (queue != null) {
                        queue.setEnabled(true);

                        Logger.print("Initiated queue `" + name + "`");
                    }

                    // Instantiate server data (which gets stored)
                    serverData = new ServerData(name);

                    Logger.print("Initiated server data `" + name + "`");
                }

                serverData.setOnlinePlayers(json.get("online-players").getAsInt());
                serverData.setMaximumPlayers(json.get("maximum-players").getAsInt());
                serverData.setWhitelisted(json.get("whitelisted").getAsBoolean());
                serverData.setLastUpdate(System.currentTimeMillis());

                List<UUID> ids = new ArrayList<>();

                for (JsonElement s : json.getAsJsonArray("wlplayers")) {
                    UUID uuid = UUID.fromString(s.getAsString());
                    ids.add(uuid);
                }
                serverData.setWhitelistedPlayers(ids);

                Logger.print("Updated data of `" + name + "`");
            }
            break;
            case CLEAR_PLAYERS: {
                Queue queue = Queue.getByName(json.get("queue").getAsString());

                if (queue == null) {
                    return;
                }

                queue.getPlayers().clear();

                Logger.print("Cleared players of `" + queue.getName() + "`");
            }
            break;
            case TOGGLE: {
                Queue queue = Queue.getByName(json.get("queue").getAsString());

                if (queue == null) {
                    return;
                }

                queue.setEnabled(!queue.isEnabled());

                Logger.print("Changed status of `" + queue.getName() + "` to " + queue.isEnabled());
            }
            break;
            case ADD_PLAYER: {
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
                queuePlayer.setInserted(System.currentTimeMillis());

                queue.getPlayers().add(queuePlayer);

                player.addProperty("inserted", queuePlayer.getInserted());

                json.addProperty("action", JedisAction.ADDED_PLAYER.name());
                json.add("player", player);

                Portal.getInstance().getBukkitPublisher().write(json);
            }
            break;
            case REMOVE_PLAYER: {
                UUID uuid = UUID.fromString(json.get("uuid").getAsString());

                Queue queue = Queue.getByPlayer(uuid);

                if (queue == null) {
                    return;
                }

                QueuePlayer queuePlayer = null;

                Iterator<QueuePlayer> iterator = queue.getPlayers().iterator();

                while (iterator.hasNext()) {
                    QueuePlayer other = iterator.next();

                    if (other.getUuid().equals(uuid)) {
                        queuePlayer = other;

                        iterator.remove();
                    }
                }

                if (queuePlayer == null) {
                    return;
                }

                JsonObject rank = new JsonObject();
                rank.addProperty("name", queuePlayer.getRank().getName());
                rank.addProperty("priority", queuePlayer.getRank().getPriority());

                JsonObject player = new JsonObject();
                player.addProperty("uuid", queuePlayer.getUuid().toString());
                player.addProperty("inserted", queuePlayer.getInserted());
                player.add("rank", rank);

                JsonObject responseData = new JsonObject();
                responseData.addProperty("action", JedisAction.REMOVED_PLAYER.name());
                responseData.addProperty("queue", queue.getName());
                responseData.add("player", player);

                Portal.getInstance().getBukkitPublisher().write(responseData);
            }
            break;
            case SEND_PLAYER_HUB: {
                String uuid = json.get("uuid").getAsString();

                    JsonObject responseData = new JsonObject();
                    responseData.addProperty("action", JedisAction.SEND_PLAYER_SERVER.name());
                    responseData.addProperty("uuid", uuid);
                    responseData.addProperty("server", "Lobby-" +
                            Portal.getInstance().getApi().getServerCache().findBestLobbyFor(UUID.fromString(uuid)).getPort());

                    Portal.getInstance().getBukkitPublisher().write(responseData);

            }
            break;
            case ADD_SERVER: {
                if (json.get("hub").getAsBoolean()) {
                   Portal.getInstance().getConfig().getHubs().add(json.get("name").getAsString());
                } else {
                    String name = json.get("name").getAsString();
                    ServerData serverData = new ServerData(name);
                    Logger.print("Initiated server data `" + name + "`");

                    serverData.setOnlinePlayers(json.get("online-players").getAsInt());
                    serverData.setMaximumPlayers(json.get("maximum-players").getAsInt());
                    serverData.setWhitelisted(json.get("whitelisted").getAsBoolean());
                    serverData.setLastUpdate(System.currentTimeMillis());

                    List<UUID> ids = new ArrayList<>();

                    for (JsonElement s : json.getAsJsonArray("wlplayers")) {
                        UUID uuid = UUID.fromString(s.getAsString());
                        ids.add(uuid);
                    }
                    serverData.setWhitelistedPlayers(ids);
                    Queue queue = new Queue(json.get("name").getAsString());
                    queue.setEnabled(true);
                    Queue.getQueues().add(queue);
                }
            }
            break;
            case REMOVE_SERVER: {
                if (json.get("hub").getAsBoolean()) {
                    Portal.getInstance().getConfig().getHubs().remove(json.get("name").getAsString());
                } else {
                    Queue.getQueues().remove(new Queue(json.get("name").getAsString()));
                    ServerData.getServers().remove(ServerData.getByName(json.get("name").getAsString()));
                }
            }
            break;
        }
    }

}
