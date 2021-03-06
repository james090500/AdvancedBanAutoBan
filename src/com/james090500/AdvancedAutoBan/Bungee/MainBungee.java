package com.james090500.AdvancedAutoBan.Bungee;

import com.james090500.AdvancedAutoBan.Bungee.Listeners.BanListenerBungee;
import com.james090500.AdvancedAutoBan.Main;
import com.james090500.AdvancedAutoBan.Managers.BanManager;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class MainBungee extends Plugin {

	@Getter
	private static MainBungee instance;

	@Override
	public void onEnable() {
		instance = this;

		Main.onEnable(getLogger());		

		//Register Events
		getProxy().getPluginManager().registerListener(this, new BanListenerBungee());

		//Load Config
		ConfigBungee.loadConfig();

		//Register Command
		getProxy().getPluginManager().registerCommand(this, new ReloadConfigCommand());

		//Periodically clear the ban list
		getProxy().getScheduler().schedule(this, () -> BanManager.cleanBanList(), 0, 60, TimeUnit.MINUTES);
	}

	class ReloadConfigCommand extends Command {
		public ReloadConfigCommand() {
			super("autoban", Main.adminPermission);
		}

		@Override
		public void execute(CommandSender commandSender, String[] strings) {
			if (commandSender.hasPermission(Main.adminPermission)) {
				ConfigBungee.loadConfig();
				commandSender.sendMessage(TextComponent.fromLegacyText("§a[AutoBan] Config has been reloaded"));
			}
		}
	}

	@Override
	public void onDisable() {
		Main.onDisable(getLogger());	 
	}

}
