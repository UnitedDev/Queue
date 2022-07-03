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
    private long startedAt;
    private List<UUID> whitelistedPlayers;

    public ServerData(String name) {
        this.name = name;
        this.startedAt = System.currentTimeMillis();
        servers.add(this);
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
