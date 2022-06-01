package fr.kohei.queue.bukkit.command.commands;

import com.google.gson.JsonObject;
import fr.kohei.BukkitAPI;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.bukkit.command.BaseCommand;
import fr.kohei.queue.shared.jedis.JedisAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceSendCommand extends BaseCommand {

    public ForceSendCommand() {
        super("forcesend");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!(BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getRank().getPermissionPower() > 100) && !commandSender.isOp()) {
                return true;
            }
        }

        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /forcesend <username> <server>");
            return true;
        }

        JsonObject json = new JsonObject();
        json.addProperty("action", JedisAction.SEND_PLAYER_SERVER.name());
        json.addProperty("username", args[0]);
        json.addProperty("server", args[1]);

        Bukkit.getScheduler().runTaskAsynchronously(Portal.getInstance(), () -> Portal.getInstance().getBukkitPublisher().write(json));

        commandSender.sendMessage(ChatColor.GREEN + "If a player with that username is online, they will be sent to `" + args[1] + "`.");

        return true;
    }

}
