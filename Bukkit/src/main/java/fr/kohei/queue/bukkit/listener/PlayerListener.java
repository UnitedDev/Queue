package fr.kohei.queue.bukkit.listener;

import com.google.gson.JsonObject;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.shared.jedis.JedisAction;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskLater(Portal.getInstance(), () -> {
            if (Bukkit.getPlayer(event.getPlayer().getUniqueId()) == null) {
                JsonObject data = new JsonObject();
                data.addProperty("action", JedisAction.REMOVE_PLAYER.name());
                data.addProperty("uuid", event.getPlayer().getUniqueId().toString());

                Bukkit.getScheduler().runTaskAsynchronously(Portal.getInstance(), () -> Portal.getInstance().getIndependentPublisher().write(data));
            }
        }, 8L);
    }

}
