package fr.kohei.queue.bukkit.thread;

import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.shared.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReminderThread extends Thread {

    @Override
    public void run() {
        while (true) {
            for (Player player : Portal.getInstance().getServer().getOnlinePlayers()) {
                Queue queue = Queue.getByPlayer(player.getUniqueId());

                if (queue != null) {
                    for (String message : Portal.getInstance().getLanguage().getReminder(player, queue)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            }

            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
