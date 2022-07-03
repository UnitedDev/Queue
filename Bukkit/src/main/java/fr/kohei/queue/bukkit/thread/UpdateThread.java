package fr.kohei.queue.bukkit.thread;

import com.google.gson.JsonObject;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.shared.jedis.JedisAction;

public class UpdateThread implements Runnable {

    @Override
    public void run() {
        try {
            JsonObject object = Portal.getInstance().getPortalServer().getServerData();
            object.addProperty("action", JedisAction.UPDATE.name());
            Portal.getInstance().getIndependentPublisher().write(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
