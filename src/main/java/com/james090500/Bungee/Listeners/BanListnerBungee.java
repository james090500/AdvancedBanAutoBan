package com.james090500.Bungee.Listeners;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.james090500.Managers.BanManager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BanListnerBungee implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandSent(ChatEvent event) {
		
		if(!event.isCommand()) {
			return;
		}
		
		CommandSender sender = (CommandSender) event.getSender();					
		
		String[] command = event.getMessage().split("\\s+");
		
		if(sender.hasPermission("ab.ban.perma") || sender.hasPermission("ab.ban.temp")) {
			if(command[0].equals("/ban") && command.length > 1) {
				ProxiedPlayer bannedPlayer = ProxyServer.getInstance().getPlayer(command[1]);
				if(bannedPlayer != null) {
					if(!bannedPlayer.hasPermission("ab.ban.exempt")) {
						BanManager.INSTANCE.addBan(bannedPlayer.getAddress().getAddress().getHostAddress(), bannedPlayer.getName());
						for(ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
							if(onlinePlayer.getAddress().getAddress().getHostAddress().equals(bannedPlayer.getAddress().getAddress().getHostAddress())) {
								onlinePlayer.disconnect();							
							}
						}
					}
				}			
			}
		}
		
		if(sender.hasPermission("ab.ban.undo")) {
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
