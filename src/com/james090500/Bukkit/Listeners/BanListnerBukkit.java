package com.james090500.Bukkit.Listeners;

import java.net.InetSocketAddress;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.james090500.Managers.BanManager;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class BanListnerBukkit implements Listener {
	
	private static String banMessage; 
	
	public BanListnerBukkit(String string) {
		banMessage = string;
	}

	/**
	 * On a ban event we need to get the IP banned
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public static void onPunishment(PlayerCommandPreprocessEvent event) {
		//Get sender and command
		Player sender = event.getPlayer();
		String[] command = event.getMessage().split("\\s+");
		
		//Make sure player has permissions and did /ban || /tempban
		if(!sender.hasPermission("ab.ban.perma") || !sender.hasPermission("ab.ban.temp") || !sender.hasPermission("ab.ban.undo")) return;
		
		if((command[0].equals("/ban") || command[0].equals("/tempban")) && command.length >= 2) {
			//Attempt to get the banned player
			Player player = Bukkit.getPlayer(command[1]);
			if(player != null) {		
				BanManager.addBan(getAddress(player), player.getName());			
	
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(getAddress(onlinePlayer).equals(getAddress(player))) {
						if(onlinePlayer.equals(player)) return;
						
						String uuid = player.getUniqueId().toString().replace("-", "");
						String banReason = banMessage.replace("%banned_player%", BanManager.getBan(getAddress(onlinePlayer)).getValue());
						
						new Punishment(player.getName(), uuid, banReason, "CapeCraft", PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();
						PunishmentManager.get().discard(player.getName());
						
						onlinePlayer.kickPlayer(banReason);
					}
				}
			}
		} else if(command[0].equals("/unban") && command.length >= 2) {
			Player player = Bukkit.getPlayer(command[1]);
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
	public static void onConnect(PlayerLoginEvent event) {
		event.getPlayer().getUniqueId();
		String uuid = event.getPlayer().getUniqueId().toString().replace("-", "");
		String ipAddr = getAddress(event.getPlayer());
		
		if(!PunishmentManager.get().isBanned(uuid)) {
			if(BanManager.checkBan(ipAddr)) {
				String banReason = banMessage.replace("%banned_player%", BanManager.getBan(ipAddr).getValue());				
				new Punishment(event.getPlayer().getName(), uuid, banReason, "CapeCraft", PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();
				PunishmentManager.get().discard(event.getPlayer().getName());
				event.disallow(Result.KICK_BANNED, banReason);
			}	
		}
	}		
	
	/**
	 * Gets the IP address from a player
	 * @param player The player
	 * @return The IP address
	 */
	private static String getAddress(Player player) {
		InetSocketAddress addr = (InetSocketAddress) player.getAddress();
		return addr.getAddress().toString();
	}
}