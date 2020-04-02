package com.james090500.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.james090500.Main;
import com.james090500.Bukkit.Listeners.BanListnerBukkit;
import com.james090500.Managers.BanManager;

public class MainBukkit extends JavaPlugin {

	@Override
	public void onEnable() {
		Main.onEnable(getLogger());		
		
		//Save Config
		saveDefaultConfig();
		
		//Register Events
		BanManager.banPeriod = getConfig().getInt("ban_period");
		getServer().getPluginManager().registerEvents(new BanListnerBukkit(getConfig().getString("ban_message")), this);
		
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			BanManager.cleanBanList();
		},(20*10L), (20*3600L));
	}
	
	@Override
	public void onDisable() {
		Main.onDisable(getLogger());
	}
	
}
