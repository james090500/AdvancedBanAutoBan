package com.james090500.Bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import com.james090500.Main;
import com.james090500.Bungee.Listeners.BanListnerBungee;
import com.james090500.Bungee.Listeners.ConnectionListenerBungee;
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
		BanManager.INSTANCE.banPeriod = loadConfig().getInt("ban_period");
		getProxy().getPluginManager().registerListener(this, new ConnectionListenerBungee(loadConfig().getString("ban_message")));
		getProxy().getPluginManager().registerListener(this, new BanListnerBungee());			
		
	
		getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
            	BanManager.INSTANCE.cleanBanList();
            }
        }, 0, 60, TimeUnit.MINUTES);
	}
	
	public Configuration loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

     
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
			return configuration;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}
}
