package com.james090500.AdvancedAutoBan.Bungee.Listeners;

import com.james090500.AdvancedAutoBan.Bungee.ConfigBungee;
import com.james090500.AdvancedAutoBan.Bungee.MainBungee;
import com.james090500.AdvancedAutoBan.Main;
import com.james090500.AdvancedAutoBan.Managers.BanManager;
import com.james090500.AdvancedAutoBan.Managers.BannedPlayer;
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

import java.net.InetSocketAddress;
import java.util.UUID;

public class BanListenerBungee implements Listener {

	/**
	 * On a ban event we need to get the IP banned
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPunishment(ChatEvent event) {
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
				if(player.hasPermission(Main.bypassPermission)) return;

				String ipAddress = getAddress(player);

				BannedPlayer bannedPlayer = new BannedPlayer(ipAddress, player.getUniqueId(), player.getName());
				BanManager.addBan(bannedPlayer);

				for(ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
					if(getAddress(onlinePlayer).equals(ipAddress)) {
						if(onlinePlayer.equals(player)) continue;
						onlinePlayer.disconnect(new ComponentBuilder(banPlayer(onlinePlayer.getName(), onlinePlayer.getUniqueId(), bannedPlayer.getUsername())).create());
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
	public void onConnect(LoginEvent event) {
		event.registerIntent(MainBungee.getInstance());
		MainBungee.getInstance().getProxy().getScheduler().runAsync(MainBungee.getInstance(), () -> {
			String uuid = event.getConnection().getUniqueId().toString().replace("-", "");
			String ipAddr = ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().toString();

			if(!PunishmentManager.get().isBanned(uuid)) {
				BannedPlayer bannedPlayer = BanManager.checkBan(ipAddr);
				if(bannedPlayer != null) {
					event.setCancelReason(new ComponentBuilder(banPlayer(event.getConnection().getName(), event.getConnection().getUniqueId(), bannedPlayer.getUsername())).create());
					event.setCancelled(true);
				}
			}
			event.completeIntent(MainBungee.getInstance());
		});
	}		
	
	/**
	 * Gets the IP address from a player
	 * @param player The player
	 * @return The IP address
	 */
	private static String getAddress(ProxiedPlayer player) {
		InetSocketAddress address = (InetSocketAddress) player.getSocketAddress();
		return address.getAddress().toString();
	}

	/**
	 * Bans the player
	 * @param username
	 * @param playerUUID
	 */
	private static String banPlayer(String username, UUID playerUUID, String bannedPlayer) {
		String uuid = playerUUID.toString().replace("-", "");
		String banReason = ConfigBungee.getBanMessage().replace("%banned_player%", bannedPlayer);
		if(ConfigBungee.getBanDuration() > -1) {
			new Punishment(username, uuid, banReason, ConfigBungee.getBanUser(), PunishmentType.TEMP_BAN, TimeManager.getTime(), ConfigBungee.getBanDuration(), null, -1).create();
		} else {
			new Punishment(username, uuid, banReason, ConfigBungee.getBanUser(), PunishmentType.BAN, TimeManager.getTime(), -1, null, -1).create();
		}
		PunishmentManager.get().discard(username);
		return banReason;
	}
}