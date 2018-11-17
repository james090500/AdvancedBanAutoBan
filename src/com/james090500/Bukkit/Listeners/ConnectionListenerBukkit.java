package com.james090500.Bukkit.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.james090500.Managers.BanManager;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class ConnectionListenerBukkit implements Listener {

	private String banMessage;
	
	public ConnectionListenerBukkit(String banMessage) {
		this.banMessage = banMessage;
	}

	@EventHandler(priority = EventPriority.LOW)
    public void onConnect(AsyncPlayerPreLoginEvent event) {
		String uuid = event.getUniqueId().toString().replace("-", "");
		String ipAddr = event.getAddress().getHostAddress();
		if(!PunishmentManager.get().isBanned(uuid)) {
			if(BanManager.INSTANCE.checkBan(ipAddr)) {								
				String banReason = banMessage.replace("%banned_player%", BanManager.INSTANCE.getBan(ipAddr).getValue());				
				new Punishment(event.getName(), uuid, banReason, "CONSOLE", PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();				
				return;
			}		
		}
	}
	
}
