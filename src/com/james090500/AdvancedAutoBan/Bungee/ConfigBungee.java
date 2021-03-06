package com.james090500.AdvancedAutoBan.Bungee;

import com.james090500.AdvancedAutoBan.Managers.BanManager;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigBungee {

    private static Configuration configuration;
    @Getter private static String banUser;
    @Getter private static int banDuration;
    @Getter private static String banMessage;

    public static void loadConfig() {
        if(!MainBungee.getInstance().getDataFolder().exists()) {
            MainBungee.getInstance().getDataFolder().mkdir();
        }

        File file = new File(MainBungee.getInstance().getDataFolder(), "config.yml");

        //Check if config exists
        if(!file.exists()) {
            try (InputStream in = ConfigBungee.class.getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Try load the config
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(MainBungee.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        banUser = configuration.getString("ban_user");
        //Keep BanUser the correct size
        banUser = banUser.substring(0, Math.min(banUser.length(), 16));

        banDuration = configuration.getInt("ban_duration");
        BanManager.banPeriod = configuration.getInt("ban_period");
        banMessage = configuration.getString("ban_message");
    }
}
