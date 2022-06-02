package fr.kohei.queue.bukkit.config;

import fr.kohei.queue.bukkit.Portal;
import fr.kohei.queue.shared.queue.Queue;
import fr.kohei.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Language {

    private String reminder = "&fFile d'attente: &c{queue} &8┃ &fPosition &c{position}&7/&c{total} &8┃ &fQuitter: &c/leavequeue";

    private List<String> added = Collections.singletonList(
            "&fVous avez rejoint la file d'attente du serveur &a{queue}&f."
    );
    private List<String> removed = Collections.singletonList(
            "&fVous avez quitté la file d'attente du serveur &c{queue}&f."
    );

    public void load() {
        FileConfiguration config = Portal.getInstance().getMainConfig().getConfig();

        if (config.contains("language.reminder")) {
            this.reminder = config.getString("language.reminder");
        }

        if (config.contains("language.added")) {
            this.added = config.getStringList("language.added");
        }

        if (config.contains("language.removed")) {
            this.removed = config.getStringList("language.removed");
        }
    }

    public List<String> getReminder(Player player, Queue queue) {
        List<String> translated = new ArrayList<>();

        translated.add(ChatColor.translateAlternateColorCodes('&', reminder
                .replace("{position}", queue.getPosition(player.getUniqueId()) + "")
                .replace("{total}", queue.getPlayers().size() + "")
                .replace("{queue}", queue.getName())));

        return translated;
    }

    public List<String> getAdded(Player player, Queue queue) {
        List<String> translated = new ArrayList<>();

        for (String line : this.added) {
            translated.add(ChatUtil.prefix(line
                    .replace("{position}", queue.getPosition(player.getUniqueId()) + "")
                    .replace("{total}", queue.getPlayers().size() + "")
                    .replace("{queue}", queue.getName()))
            );
        }

        return translated;
    }

    public List<String> getRemoved(Queue queue) {
        List<String> translated = new ArrayList<>();

        for (String line : this.removed) {
            translated.add(ChatUtil.prefix(line
                    .replace("{queue}", queue.getName()))
            );
        }

        return translated;
    }

}
