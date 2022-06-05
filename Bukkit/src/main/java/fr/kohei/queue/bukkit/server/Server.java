package fr.kohei.queue.bukkit.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.kohei.BukkitAPI;
import fr.kohei.queue.bukkit.Portal;
import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Server {

    private String name;
    private boolean hub;

    public JsonObject getServerData() {
        JsonObject object = new JsonObject();
        object.addProperty("name", this.name);
        object.addProperty("hub", hub);
        object.addProperty("online-players", Portal.getInstance().getServer().getOnlinePlayers().size());
        object.addProperty("maximum-players", Portal.getInstance().getServer().getMaxPlayers());

        object.addProperty("whitelisted", !hub && BukkitAPI.getCommonAPI().getServerCache().getUhcServers().get(Bukkit.getPort()).isWhitelisted());

        JsonArray array = new JsonArray();


        if (!hub) {
            for (UUID uuid : BukkitAPI.getCommonAPI().getServerCache().getUhcServers().get(Bukkit.getPort()).getWhitelistedPlayers()) {
                array.add(new JsonPrimitive(uuid.toString()));
            }
        }


        object.add("wlplayers", array);
        return object;
    }

}
