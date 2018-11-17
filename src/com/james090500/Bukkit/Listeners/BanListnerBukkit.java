package com.james090500.Bukkit.Listeners;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.james090500.Managers.BanManager;

public class BanListnerBukkit implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandSent(PlayerCommandPreprocessEvent event) {
		
		String[] command = event.getMessage().split("\\s+");
		
		if(event.getPlayer().hasPermission("ab.ban.perma") || event.getPlayer().hasPermission("ab.ban.temp")) {
			if(command[0].equals("/ban") && command.length > 1) {
				Player bannedPlayer = Bukkit.getPlayer(command[1]);
				if(bannedPlayer != null) {
					if(!bannedPlayer.hasPermission("ab.ban.exempt")) {
						BanManager.INSTANCE.addBan(bannedPlayer.getAddress().getAddress().getHostAddress(), bannedPlayer.getName());
						for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
							if(onlinePlayer.getAddress().getAddress().getHostAddress().equals(bannedPlayer.getAddress().getAddress().getHostAddress())) {
								onlinePlayer.kickPlayer(null);							
							}
						}
					}
				}			
			}
		}
		
		if(event.getPlayer().hasPermission("ab.ban.undo")) {
			if(command[0].equals("/unban") && command.length > 1) {
				String bannedPlayer = command[1];
				
				Iterator<Entry<String, Entry<Long, String>>> banIt = BanManager.INSTANCE.getBans().entrySet().iterator();
				while(banIt.hasNext()) {
					Map.Entry<String, Entry<Long, String>> pair = (Map.Entry<String, Entry<Long, String>>)banIt.next();
					if(pair.getValue().getValue().equals(bannedPlayer)) {
						BanManager.INSTANCE.removeBan(pair.getKey());
					}
				}	
			}
		}
	}
}
