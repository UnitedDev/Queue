package fr.kohei.queue.bukkit.command.commands;


import fr.kohei.BukkitAPI;
import fr.kohei.queue.bukkit.command.BaseCommand;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.queue.shared.server.ServerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DataDumpCommand extends BaseCommand {

    public DataDumpCommand() {
        super("datadump");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!(BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getRank().getPermissionPower() > 100) && !commandSender.isOp()) {
                return true;
            }
        }


        commandSender.sendMessage("Servers:");

        for (ServerData serverData : ServerData.getServers()) {
            StringBuilder builder = new StringBuilder("- ")
                    .append(serverData.getName())
                    .append(" (")
                    .append(serverData.isOnline())
                    .append(") (")
                    .append(serverData.getOnlinePlayers())
                    .append("/")
                    .append(serverData.getMaximumPlayers())
                    .append(")");

            commandSender.sendMessage(builder.toString());
        }

        commandSender.sendMessage("Queues:");

        for (Queue queue : Queue.getQueues()) {
            StringBuilder builder = new StringBuilder("- ")
                    .append(queue.getName())
                    .append(" (")
                    .append(queue.getPlayers().size())
                    .append(" en queue)");

            ServerData serverData = queue.getServerData();

            if (serverData == null) {
                builder
                        .append(" (offline)");
            } else {
                builder
                        .append(" (")
                        .append(serverData.isOnline())
                        .append(") (")
                        .append(serverData.isWhitelisted())
                        .append(") (")
                        .append(serverData.getOnlinePlayers())
                        .append("/")
                        .append(serverData.getMaximumPlayers())
                        .append(")");
            }

            commandSender.sendMessage(builder.toString());
        }

        return true;
    }

}
