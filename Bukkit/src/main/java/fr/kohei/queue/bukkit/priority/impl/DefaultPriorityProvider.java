package fr.kohei.queue.bukkit.priority.impl;

import fr.kohei.BukkitAPI;
import fr.kohei.common.cache.Rank;
import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.bukkit.util.MapUtil;
import fr.kohei.queue.bukkit.priority.PriorityProvider;
import fr.kohei.queue.shared.queue.QueueRank;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DefaultPriorityProvider implements PriorityProvider {

    private static final Portal plugin = Portal.getInstance();

    private Map<Rank, QueueRank> priorities = new HashMap<>();

    public DefaultPriorityProvider() {
        try {
            for (Rank rank : BukkitAPI.getCommonAPI().getRanks()) {
                this.priorities.put(rank, new QueueRank(rank.getToken(), rank.getPermissionPower()));
            }

            this.priorities = MapUtil.sortByValue(this.priorities);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to configure default priority provider.");
            e.printStackTrace();
        }
    }

    @Override
    public QueueRank getPriority(Player player) {
        return priorities.get(BukkitAPI.getCommonAPI().getProfile(player.getUniqueId()).getRank());
    }

}
