package fr.kohei.queue.shared.server;

import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class ServerData {

    @Getter
    private static Set<ServerData> servers = new HashSet<>();

    private String name;
    private int onlinePlayers;
    private int maximumPlayers;
    private boolean whitelisted;
    private long lastUpdate;
    private List<UUID> whitelistedPlayers;

    public ServerData(String name) {
        this.name = name;
        servers.add(this);
    }

    public boolean isOnline() {
        return System.currentTimeMillis() - this.lastUpdate < 15000L;
    }

    public static ServerData getByName(String name) {
        for (ServerData server : servers) {
            if (server.getName().equalsIgnoreCase(name)) {
                return server;
            }
        }

        return null;
    }

}
