package com.james090500.AdvancedAutoBan.Bukkit;

import com.james090500.AdvancedAutoBan.Bukkit.Listeners.BanListenerBukkit;
import com.james090500.AdvancedAutoBan.Main;
import com.james090500.AdvancedAutoBan.Managers.BanManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MainBukkit extends JavaPlugin implements CommandExecutor {

	@Getter
	private static MainBukkit instance;

	@Override
	public void onEnable() {
		instance = this;

		Main.onEnable(getLogger());		
		
		//Save Config
		saveDefaultConfig();

		//Load Config
		ConfigBukkit.loadConfig();

		//Register Command
		getCommand("autoban").setExecutor(this);

		//Register Events
		getServer().getPluginManager().registerEvents(new BanListenerBukkit(), this);
		
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			BanManager.cleanBanList();
		},(20*10L), (20*3600L));
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender.hasPermission(Main.adminPermission)) {
			ConfigBukkit.loadConfig();
			sender.sendMessage("§a[AutoBan] Config has been reloaded");
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDisable() {
		Main.onDisable(getLogger());
	}
	
}
