package fr.kohei.queue.bukkit.thread;

import com.google.gson.JsonObject;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.shared.jedis.JedisAction;

public class UpdateThread extends Thread {

    @Override
    public void run() {
        while (true) {
            JsonObject object = Portal.getInstance().getPortalServer().getServerData();
            object.addProperty("action", JedisAction.UPDATE.name());
            Portal.getInstance().getIndependentPublisher().write(object);

            try {
                Thread.sleep(2500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
