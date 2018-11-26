package com.james090500.Bukkit.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.james090500.Managers.BanManager;

import me.leoko.advancedban.AdvancedBan;
import me.leoko.advancedban.punishment.Punishment;
import me.leoko.advancedban.punishment.PunishmentType;

public class ConnectionListenerBukkit implements Listener {

	private AdvancedBan advancedBan;
	private String banMessage;
	
	public ConnectionListenerBukkit(String banMessage) {
		advancedBan = AdvancedBan.get();
		this.banMessage = banMessage;
	}

	@EventHandler(priority = EventPriority.LOW)
    public void onConnect(AsyncPlayerPreLoginEvent event) {
		String uuid = event.getUniqueId().toString().replace("-", "");
		String ipAddr = event.getAddress().getHostAddress();
		if(!advancedBan.getPunishmentManager().isBanned(uuid)) {
			if(BanManager.INSTANCE.checkBan(ipAddr)) {								
				String banReason = banMessage.replace("%banned_player%", BanManager.INSTANCE.getBan(ipAddr).getValue());								
				Punishment punishment = new Punishment(uuid, event.getName(), "CONSOLE", null, advancedBan.getTimeManager().getTime(), -1, PunishmentType.BAN);
				punishment.setReason(banReason);
				advancedBan.getPunishmentManager().addPunishment(punishment);
				return;
			}		
		}
	}
	
}
