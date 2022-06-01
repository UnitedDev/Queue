package fr.kohei.queue.independent.thread;

import fr.kohei.queue.independent.Portal;
import com.google.gson.JsonObject;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.queue.shared.queue.QueuePlayer;
import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.server.ServerData;

public class QueueThread extends Thread {

    private static final Long SEND_DELAY = 500L;

    @Override
    public void run() {
        while (true) {
            for (Queue queue : Queue.getQueues()) {
                ServerData serverData = queue.getServerData();

                if (serverData == null) {
                    continue;
                }

                if (!queue.isEnabled()) {
                    continue;
                }

                if (!serverData.isOnline()) {
                    continue;
                }
                QueuePlayer next = queue.getPlayers().peek();

                if (next == null) continue;

                if (serverData.isWhitelisted()) {
                    if(!serverData.getWhitelistedPlayers().contains(next.getUuid())) {
                        continue;
                    }
                }

                if (serverData.getOnlinePlayers() >= serverData.getMaximumPlayers()) {
                    continue;
                }


                queue.getPlayers().poll();
                JsonObject data = new JsonObject();
                data.addProperty("action", JedisAction.SEND_PLAYER_SERVER.name());
                data.addProperty("server", queue.getName());
                data.addProperty("uuid", next.getUuid().toString());

                Portal.getInstance().getBukkitPublisher().write(data);
            }

            try {
                Thread.sleep(SEND_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
