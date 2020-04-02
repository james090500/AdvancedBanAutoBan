package com.james090500.Bungee.Listeners;

import java.net.InetSocketAddress;
import java.util.Map.Entry;

import com.james090500.Managers.BanManager;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BanListnerBungee implements Listener {
	
	private static String banMessage; 
	
	public BanListnerBungee(String string) {
		banMessage = string;
	}

	/**
	 * On a ban event we need to get the IP banned
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public static void onPunishment(ChatEvent event) {
		//Make sure is a command
		if(!event.isCommand()) return;
		
		//Get sender and command
		CommandSender sender = (CommandSender) event.getSender();
		String[] command = event.getMessage().split("\\s+");
		
		//Make sure player has permissions and did /ban || /tempban
		if(!sender.hasPermission("ab.ban.perma") || !sender.hasPermission("ab.ban.temp") || !sender.hasPermission("ab.ban.undo")) return;
		
		if((command[0].equals("/ban") || command[0].equals("/tempban")) && command.length >= 2) {
			//Attempt to get the banned player
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(command[1]);
			if(player != null) {		
				BanManager.addBan(getAddress(player), player.getName());			
	
				for(ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
					if(getAddress(onlinePlayer).equals(getAddress(player))) {
						if(onlinePlayer.equals(player)) return;
						
						String uuid = player.getUniqueId().toString().replace("-", "");
						String banReason = banMessage.replace("%banned_player%", BanManager.getBan(getAddress(onlinePlayer)).getValue());
						
						new Punishment(player.getName(), uuid, banReason, "CapeCraft", PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();
						PunishmentManager.get().discard(player.getName());
						
						onlinePlayer.disconnect(new ComponentBuilder(banReason).create());
					}
				}
			}
		} else if(command[0].equals("/unban") && command.length >= 2) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(command[1]);
			if(player != null) {
				for(Entry<String, Entry<Long, String>> entry : BanManager.getBans().entrySet()) {
					Entry<Long, String> playerInfo = entry.getValue();
					if(playerInfo.getValue().equalsIgnoreCase(player.getName())) {
						BanManager.removeBan(entry.getKey());
					}
				}
			}
		}
	}
	
	/**
	 * First thing, check if a players IP is banned. If so lets rid of them
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public static void onConnect(LoginEvent event) {
		event.getConnection().getUniqueId();
		String uuid = event.getConnection().getUniqueId().toString().replace("-", "");
		String ipAddr = ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().toString();
		
		if(!PunishmentManager.get().isBanned(uuid)) {
			if(BanManager.checkBan(ipAddr)) {
				String banReason = banMessage.replace("%banned_player%", BanManager.getBan(ipAddr).getValue());				
				new Punishment(event.getConnection().getName(), uuid, banReason, "CapeCraft", PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();
				PunishmentManager.get().discard(event.getConnection().getName());
				event.setCancelReason(new ComponentBuilder(banReason).create());
				event.setCancelled(true);
			}	
		}
	}		
	
	/**
	 * Gets the IP address from a player
	 * @param player The player
	 * @return The IP address
	 */
	private static String getAddress(ProxiedPlayer player) {
		InetSocketAddress addr = (InetSocketAddress) player.getSocketAddress();
		return addr.getAddress().toString();
	}
}