package com.james090500.AdvancedAutoBan.Managers;

import com.james090500.AdvancedAutoBan.Main;
import lombok.Getter;

import java.util.UUID;

public class BannedPlayer {

    @Getter private String ip;
    @Getter private UUID uuid;
    @Getter private String username;
    @Getter private long timeBanned;

    public BannedPlayer(String ip, UUID uuid, String username) {
        this.ip = ip;
        this.uuid = uuid;
        this.username = username;
        this.timeBanned = System.currentTimeMillis() / 1000;
    }

    @Override
    public String toString() {
        return "BannedPlayer{" +
                "ip='" + ip + '\'' +
                ", uuid=" + uuid +
                ", username='" + username + '\'' +
                ", timeBanned=" + timeBanned +
            '}';
    }
}
