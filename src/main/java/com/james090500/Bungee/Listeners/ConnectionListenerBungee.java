package com.james090500.Bungee.Listeners;

import com.james090500.Managers.BanManager;

import me.leoko.advancedban.AdvancedBan;
import me.leoko.advancedban.punishment.Punishment;
import me.leoko.advancedban.punishment.PunishmentType;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ConnectionListenerBungee implements Listener {

	private AdvancedBan advancedBan;
	private String banMessage;
	
	public ConnectionListenerBungee(String banMessage) {
		advancedBan = AdvancedBan.get();
		this.banMessage = banMessage;
	}

	@EventHandler(priority = EventPriority.LOW)
    public void onConnect(LoginEvent event) {
		event.getConnection().getUniqueId();
		String uuid = event.getConnection().getUniqueId().toString().replace("-", "");
		String ipAddr = event.getConnection().getAddress().getAddress().getHostAddress();
		
		if(!advancedBan.getPunishmentManager().isBanned(uuid)) {			
			if(BanManager.INSTANCE.checkBan(ipAddr)) {				
				String banReason = banMessage.replace("%banned_player%", BanManager.INSTANCE.getBan(ipAddr).getValue());								
				Punishment punishment = new Punishment(uuid, event.getConnection().getName(), "CONSOLE", null, advancedBan.getTimeManager().getTime(), -1, PunishmentType.BAN);
				punishment.setReason(banReason);
				advancedBan.getPunishmentManager().addPunishment(punishment);
			}		
		}
	}
	
}
