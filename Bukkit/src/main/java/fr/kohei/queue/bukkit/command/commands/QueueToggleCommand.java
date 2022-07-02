package fr.kohei.queue.bukkit.command.commands;

import com.google.gson.JsonObject;
import fr.kohei.BukkitAPI;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.bukkit.command.BaseCommand;
import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.queue.Queue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class QueueToggleCommand extends BaseCommand {

    public QueueToggleCommand() {
        super("queuetoggle");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!(BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getRank().getPermissionPower() > 50) && !commandSender.isOp()) {
                return true;
            }
        }

        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /queuetoggle <server>");
            return true;
        }

        Queue queue = Queue.getByName(args[0]);

        if (queue == null) {
            commandSender.sendMessage(ChatColor.RED + "That queue does not exist.");
            return true;
        }

        queue.setEnabled(!queue.isEnabled());

        JsonObject json = new JsonObject();
        json.addProperty("action", JedisAction.TOGGLE.name());
        json.addProperty("queue", queue.getName());

        Bukkit.getScheduler().runTaskAsynchronously(Portal.getInstance(), () -> Portal.getInstance().getIndependentPublisher().write(json));

        commandSender.sendMessage(ChatColor.GREEN + "Changed status of `" + queue.getName() + "` to " + queue.isEnabled());

        return true;
    }

}
