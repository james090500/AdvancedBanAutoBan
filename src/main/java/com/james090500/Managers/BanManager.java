package com.james090500.Managers;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BanManager {
	
	//Keeps everything in the same instance
	public static final BanManager INSTANCE = new BanManager();	
	
	//List of recent bans inside of grace
	//IP, time, name
	private HashMap<String, Entry<Long, String>> recentBans = new HashMap<>();
	
	//Default Ban Period
	public int banPeriod;
	
	public HashMap<String, Entry<Long, String>> getBans() { 
		return recentBans;
	}
	
	//Returns if the IP in question is in the ban or not
	//If it is in the recentBan list and the bantime + ban period is more than the current time in seconds return true
	//else return false and remove it from the banmanager
	public boolean checkBan(String ip) {
		if(recentBans.containsKey(ip)) {
			if((recentBans.get(ip).getKey() + banPeriod) > System.currentTimeMillis() / 1000) {
				return true;
			} else {
				recentBans.remove(ip);
				return false;
			}
		}
		return false;
	}

	//Return the ban
	public Entry<Long, String> getBan(String ip) {
		if(checkBan(ip)) {
			return recentBans.get(ip);
		}
		return null;		
	}
	
	//Adds an IP to the recently banned
    public void addBan(String ip, String playerName){    	
    	Entry<Long, String> playerInfo = new SimpleEntry<Long, String>(System.currentTimeMillis() / 1000, playerName);
        recentBans.put(ip, playerInfo);
    }

	public void removeBan(String key) {
		recentBans.remove(key);
	}

	public void cleanBanList() {
		Iterator<Entry<String, Entry<Long, String>>> banIt = BanManager.INSTANCE.getBans().entrySet().iterator();
		while(banIt.hasNext()) {
			Map.Entry<String, Entry<Long, String>> pair = (Map.Entry<String, Entry<Long, String>>)banIt.next();			
			if((pair.getValue().getKey() + banPeriod) > System.currentTimeMillis() / 1000) {
				BanManager.INSTANCE.removeBan(pair.getKey());
			}
		}			
	}	       
}
