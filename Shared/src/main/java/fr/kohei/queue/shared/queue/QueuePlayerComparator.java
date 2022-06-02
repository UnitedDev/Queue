package fr.kohei.queue.shared.queue;

import java.util.Comparator;

public class QueuePlayerComparator implements Comparator<QueuePlayer> {

    @Override
    public int compare(QueuePlayer firstPlayer, QueuePlayer secondPlayer) {
        return -firstPlayer.compareTo(secondPlayer);
    }

}