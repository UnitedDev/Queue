package fr.kohei.queue.bukkit.priority;

import fr.kohei.queue.shared.queue.QueueRank;
import org.bukkit.entity.Player;

public interface PriorityProvider {

    QueueRank getPriority(Player player);

}
