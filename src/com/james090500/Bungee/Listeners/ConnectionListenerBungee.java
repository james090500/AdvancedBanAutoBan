package com.james090500.Bungee.Listeners;

import com.james090500.Managers.BanManager;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ConnectionListenerBungee implements Listener {

	private String banMessage;
	
	public ConnectionListenerBungee(String banMessage) {
		this.banMessage = banMessage;
	}

	@EventHandler(priority = EventPriority.LOW)
    public void onConnect(LoginEvent event) {
		event.getConnection().getUniqueId();
		String uuid = event.getConnection().getUniqueId().toString().replace("-", "");
		String ipAddr = event.getConnection().getAddress().getAddress().getHostAddress();
		
		if(!PunishmentManager.get().isBanned(uuid)) {			
			if(BanManager.INSTANCE.checkBan(ipAddr)) {				
				String banReason = banMessage.replace("%banned_player%", BanManager.INSTANCE.getBan(ipAddr).getValue());				
				new Punishment(event.getConnection().getName(), uuid, banReason, "CONSOLE", PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();				
			}		
		}
	}
	
}
