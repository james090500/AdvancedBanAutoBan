package com.james090500.AdvancedAutoBan.Bukkit;

import com.james090500.AdvancedAutoBan.Managers.BanManager;
import lombok.Getter;

public class ConfigBukkit {

    @Getter private static String banUser;
    @Getter private static int banDuration;
    @Getter private static String banMessage;

    public static void loadConfig() {
        banUser = MainBukkit.getInstance().getConfig().getString("ban_user");
        //Keep BanUser the correct size
        banUser = banUser.substring(0, Math.min(banUser.length(), 16));

        banDuration = MainBukkit.getInstance().getConfig().getInt("ban_duration");
        BanManager.banPeriod = MainBukkit.getInstance().getConfig().getInt("ban_period");
        banMessage = MainBukkit.getInstance().getConfig().getString("ban_message");
    }
}
