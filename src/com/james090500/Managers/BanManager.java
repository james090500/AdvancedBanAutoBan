package com.james090500.Managers;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BanManager {	
	
	//List of recent bans inside of grace
	//IP, time, name
	private static HashMap<String, Entry<Long, String>> recentBans = new HashMap<>();
	
	//Default Ban Period
	public static int banPeriod;
	
	/**
	 * Returns the ban list
	 * @return
	 */
	public static HashMap<String, Entry<Long, String>> getBans() { 
		return recentBans;
	}
	
	/**
	 * Check if the IP provided has been previously
	 * @param ip
	 * @return
	 */
	public static boolean checkBan(String ip) {
		if(recentBans.containsKey(ip)) {
			if((recentBans.get(ip).getKey() + 600) > System.currentTimeMillis() / 1000) {
				return true;
			} else {
				recentBans.remove(ip);
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Check if the connecting player is in the ban list
	 * @param ip Connecting Players IP
	 * @return
	 */
	public static Entry<Long, String> getBan(String ip) {
		if(checkBan(ip)) {
			return recentBans.get(ip);
		}
		return null;		
	}
	
	/**
	 * Adds a ban to the ban list
	 * @param ip IP To add
	 * @param playerName Player who was banned
	 */
    public static void addBan(String ip, String playerName){    	
    	Entry<Long, String> playerInfo = new SimpleEntry<Long, String>(System.currentTimeMillis() / 1000, playerName);
        recentBans.put(ip, playerInfo);
    }

    /**
     * Removes a ban from the ban list
     * @param key IPv4 Address
     */
	public static void removeBan(String key) {
		recentBans.remove(key);
	}

	/**
	 * Removes dead entries from the ban list
	 */
	public static void cleanBanList() {
		Iterator<Entry<String, Entry<Long, String>>> banIt = getBans().entrySet().iterator();
		while(banIt.hasNext()) {
			Map.Entry<String, Entry<Long, String>> pair = (Map.Entry<String, Entry<Long, String>>)banIt.next();			
			if((pair.getValue().getKey() + banPeriod) > System.currentTimeMillis() / 1000) {
				removeBan(pair.getKey());
			}
		}			
	}	       
}
