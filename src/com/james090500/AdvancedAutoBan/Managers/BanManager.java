package com.james090500.AdvancedAutoBan.Managers;

import com.james090500.AdvancedAutoBan.Main;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BanManager {	
	
	//List of recent bans inside of grace
	@Getter private static HashMap<String, BannedPlayer> recentBans = new HashMap<>();

	//Default Ban Period
	public static int banPeriod;

	/**
	 * Check if the IP provided has been previously
	 * @param ip
	 * @return
	 */
	public static BannedPlayer checkBan(String ip) {
		BannedPlayer bannedPlayer = recentBans.get(ip);
		if(bannedPlayer != null) {
			if((bannedPlayer.getTimeBanned() + banPeriod) > System.currentTimeMillis() / 1000) {
				return bannedPlayer;
			} else {
				recentBans.remove(ip);
				return null;
			}
		}
		return null;
	}

	/**
	 * Adds a ban to the ban list
	 * @param bannedPlayer The banned player instance
	 */
    public static void addBan(BannedPlayer bannedPlayer) {
        recentBans.put(bannedPlayer.getIp(), bannedPlayer);
    }

	/**
	 * Removes a player by username
	 * @param username
	 */
	public static void unbanUsername(String username) {
		List<String> toRemove = new ArrayList<>();
		getRecentBans().forEach((ip, bannedPlayer) -> {
			if(bannedPlayer.getUsername().equals(username)) {
				toRemove.add(bannedPlayer.getIp());
			}
		});

		toRemove.forEach(ip -> {
			recentBans.remove(ip);
		});
	}

	/**
	 * Removes dead entries from the ban list
	 */
	public static void cleanBanList() {
		getRecentBans().forEach((ip, bannedPlayer) -> {
			if((bannedPlayer.getTimeBanned() + banPeriod) < System.currentTimeMillis() / 1000) {
				recentBans.remove(ip);
			}
		});
	}	       
}
