package fr.kohei.queue.independent.thread;

import fr.kohei.queue.independent.Portal;
import fr.kohei.queue.independent.log.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.queue.shared.queue.QueuePlayer;
import fr.kohei.queue.shared.server.ServerData;
import com.google.gson.JsonPrimitive;

import java.util.UUID;

public class BroadcastThread extends Thread {

    @Override
    public void run() {
        while (true) {
            JsonArray servers = new JsonArray();
            JsonArray queues = new JsonArray();

            for (ServerData serverData : ServerData.getServers()) {
                JsonObject data = new JsonObject();
                data.addProperty("name", serverData.getName());
                data.addProperty("online-players", serverData.getOnlinePlayers());
                data.addProperty("maximum-players", serverData.getMaximumPlayers());
                data.addProperty("whitelisted", serverData.isWhitelisted());

                JsonArray array = new JsonArray();

                for (UUID id: serverData.getWhitelistedPlayers()) {
                    array.add(new JsonPrimitive(id.toString()));
                }
                data.add("wlplayers", array);
                servers.add(data);
            }

            for (Queue queue : Queue.getQueues()) {
                JsonArray players = new JsonArray();

                for (QueuePlayer player : queue.getPlayers()) {
                    JsonObject rank = new JsonObject();
                    rank.addProperty("name", player.getRank().getName());
                    rank.addProperty("priority", player.getRank().getPriority());

                    JsonObject json = new JsonObject();
                    json.addProperty("uuid", player.getUuid().toString());
                    json.addProperty("inserted", player.getInserted());
                    json.add("rank", rank);

                    players.add(json);
                }

                JsonObject json = new JsonObject();
                json.addProperty("name", queue.getName());
                json.addProperty("status", queue.isEnabled());
                json.add("players", players);

                queues.add(json);
            }

            JsonObject json = new JsonObject();
            json.addProperty("action", JedisAction.LIST.name());
            json.add("servers", servers);
            json.add("queues", queues);

            Portal.getInstance().getBukkitPublisher().write(json);

            Logger.print("Broadcasted server and queue list");

            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
