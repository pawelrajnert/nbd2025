package org.padan.Model;

import redis.clients.jedis.Jedis;

import java.io.InputStream;
import java.util.Properties;

public class RedisConnect {
    private static String hostname;
    private static int port;

    static {
        try (InputStream inputStream = RedisConnect.class.getClassLoader().getResourceAsStream("redis.properties")) {
            Properties prop = new Properties();
            prop.load(inputStream);
            hostname = prop.getProperty("redis.host");
            port = Integer.parseInt(prop.getProperty("redis.port"));
            if (hostname == null || hostname.isEmpty()) {
                throw new RuntimeException("Redis hostname is mandatory");
            }

            if (port <= 0) {
                throw new RuntimeException("Redis port is mandatory");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load Redis connection configuration", e);
        }
    }

    public static Jedis startConnection() {
        return new Jedis(hostname, port);
    }
}
