package fr.kohei.queue.bukkit.command.commands;

import com.google.gson.JsonObject;
import fr.kohei.BukkitAPI;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.bukkit.command.BaseCommand;
import fr.kohei.queue.shared.jedis.JedisAction;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.queue.shared.queue.QueueRank;
import fr.kohei.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class JoinQueueCommand extends BaseCommand {

    public JoinQueueCommand() {
        super("joinqueue");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(CONSOLE_SENDER);
            return true;
        }

        if (args.length == 0) {
            commandSender.sendMessage(ChatUtil.prefix("&cMerci d'utiliser &c/joinqueue <server>"));
            return true;
        }

        Player bukkitPlayer = (Player) commandSender;

        Queue queue = Queue.getByPlayer(bukkitPlayer.getUniqueId());

        if (queue != null) {
            bukkitPlayer.sendMessage(ChatUtil.prefix("&cVous êtes déjà dans une file d'attente."));
            return true;
        }

        queue = Queue.getByName(args[0]);

        if (queue == null) {
            bukkitPlayer.sendMessage(ChatUtil.prefix("&cCette file d'attente n'existe pas."));
            return true;
        }

        if (queue.getServerData() == null) {
            bukkitPlayer.sendMessage(ChatUtil.prefix("&cCe serveur est hors-ligne."));
            return true;
        }

        if (BukkitAPI.getCommonAPI().getProfile(bukkitPlayer.getUniqueId()).getRank().getPermissionPower() > 50) {
            JsonObject data = new JsonObject();
            data.addProperty("action", JedisAction.SEND_PLAYER_SERVER.name());
            data.addProperty("uuid", bukkitPlayer.getUniqueId().toString());
            data.addProperty("server", queue.getName());

            Bukkit.getScheduler().runTaskAsynchronously(Portal.getInstance(), () -> Portal.getInstance().getBukkitPublisher().write(data));

            return true;
        }

        QueueRank queueRank = Portal.getInstance().getPriorityProvider().getPriority(bukkitPlayer);

        JsonObject rank = new JsonObject();
        rank.addProperty("name", queueRank.getName());
        rank.addProperty("priority", queueRank.getPriority());

        JsonObject player = new JsonObject();
        player.addProperty("uuid", bukkitPlayer.getUniqueId().toString());
        player.add("rank", rank);

        JsonObject data = new JsonObject();
        data.addProperty("action", JedisAction.ADD_PLAYER.name());
        data.addProperty("queue", queue.getName());
        data.add("player", player);

        Bukkit.getScheduler().runTaskAsynchronously(Portal.getInstance(), () -> Portal.getInstance().getIndependentPublisher().write(data));

        return true;
    }

}
