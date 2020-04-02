package com.james090500.Bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import com.james090500.Main;
import com.james090500.Bungee.Listeners.BanListnerBungee;
import com.james090500.Managers.BanManager;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class MainBungee extends Plugin {
	
	@Override
	public void onEnable() {
		Main.onEnable(getLogger());		

		//Register Events
		BanManager.banPeriod = loadConfig().getInt("ban_period");
		getProxy().getPluginManager().registerListener(this, new BanListnerBungee(loadConfig().getString("ban_message")));			
		
		//Periodically clear the ban list
		getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
            	BanManager.cleanBanList();
            }
        }, 0, 60, TimeUnit.MINUTES);
	}
	
	@Override
	public void onDisable() {
		Main.onDisable(getLogger());	 
	}
	
	/**
	 * Loads the config
	 * @return
	 */
	public Configuration loadConfig() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        
        File file = new File(getDataFolder(), "config.yml");

        //Check if config exists
        if(!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        //Try load the config
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
			return configuration;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}
}
