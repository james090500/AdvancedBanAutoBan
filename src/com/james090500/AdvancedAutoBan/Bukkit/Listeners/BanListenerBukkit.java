package com.james090500.AdvancedAutoBan.Bukkit.Listeners;

import com.james090500.AdvancedAutoBan.Bukkit.ConfigBukkit;
import com.james090500.AdvancedAutoBan.Main;
import com.james090500.AdvancedAutoBan.Managers.BanManager;
import com.james090500.AdvancedAutoBan.Managers.BannedPlayer;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.net.InetSocketAddress;
import java.util.UUID;

public class BanListenerBukkit implements Listener {

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
				if(player.hasPermission(Main.bypassPermission)) return;

				String ipAddress = getAddress(player);

				BannedPlayer bannedPlayer = new BannedPlayer(ipAddress, player.getUniqueId(), player.getName());
				BanManager.addBan(bannedPlayer);
	
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(getAddress(onlinePlayer).equals(ipAddress)) {
						if(onlinePlayer.equals(player)) continue;
						onlinePlayer.kick(Component.text(banPlayer(onlinePlayer.getName(), onlinePlayer.getUniqueId(), bannedPlayer.getUsername())));
					}
				}
			}
		} else if(command[0].equals("/unban") && command.length >= 2) {
			BanManager.unbanUsername(command[1]);
		}
	}
	
	/**
	 * First thing, check if a players IP is banned. If so lets rid of them
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public static void onConnect(PlayerLoginEvent event) {
		String uuid = event.getPlayer().getUniqueId().toString().replace("-", "");
		String ipAddr = getAddress(event.getPlayer());
		
		if(!PunishmentManager.get().isBanned(uuid)) {
			BannedPlayer bannedPlayer = BanManager.checkBan(ipAddr);
			if(bannedPlayer != null) {
				event.disallow(Result.KICK_BANNED, Component.text(banPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId(), bannedPlayer.getUsername())));
			}	
		}
	}		
	
	/**
	 * Gets the IP address from a player
	 * @param player The player
	 * @return The IP address
	 */
	private static String getAddress(Player player) {
		InetSocketAddress addr = player.getAddress();
		return addr.getAddress().toString();
	}

	/**
	 * Bans the player
	 * @param username
	 * @param playerUUID
	 */
	private static String banPlayer(String username, UUID playerUUID, String bannedPlayer) {
		String uuid = playerUUID.toString().replace("-", "");
		String banReason = ConfigBukkit.getBanMessage().replace("%banned_player%", bannedPlayer);
		if(ConfigBukkit.getBanDuration() > -1) {
			new Punishment(username, uuid, banReason, ConfigBukkit.getBanUser(), PunishmentType.TEMP_BAN, TimeManager.getTime(), ConfigBukkit.getBanDuration(), null, -1).create();
		} else {
			new Punishment(username, uuid, banReason, ConfigBukkit.getBanUser(), PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();
		}
		PunishmentManager.get().discard(username);
		return banReason;
	}
}