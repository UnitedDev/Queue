package fr.kohei.queue.bukkit.command.commands;

import com.google.gson.JsonObject;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.bukkit.command.BaseCommand;
import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveQueueCommand extends BaseCommand {

    public LeaveQueueCommand() {
        super("leavequeue");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(CONSOLE_SENDER);
            return true;
        }

        Player player = (Player) commandSender;

        Queue queue = Queue.getByPlayer(player.getUniqueId());

        if (queue == null) {
            player.sendMessage(ChatUtil.prefix("&cVous n'êtes dans aucune file d'attente."));
            return true;
        }

        JsonObject data = new JsonObject();
        data.addProperty("action", JedisAction.REMOVE_PLAYER.name());
        data.addProperty("uuid", player.getUniqueId().toString());

        Bukkit.getScheduler().runTaskAsynchronously(Portal.getInstance(), () -> Portal.getInstance().getIndependentPublisher().write(data));

        return true;
    }

}
