package fr.kohei.queue.independent.file;

import fr.kohei.queue.shared.util.redis.JedisSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Getter
public class Config {
    @Setter
    private List<String> hubs;
    private String redisHost;
    private int redisPort;
    private String redisPassword;

    public Config(String redisHost, int redisPort, String redisPassword) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
        this.hubs = new ArrayList<>();
    }

    public JedisSettings getJedisSettings() {
        return new JedisSettings(redisHost, redisPort, redisPassword);
    }
}
