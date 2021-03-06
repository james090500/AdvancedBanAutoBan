package com.james090500.AdvancedAutoBan;

import lombok.Getter;

import java.util.logging.Logger;

public class Main {

	@Getter private static Logger logger;
	public static String adminPermission = "autoban.admin";
	public static String bypassPermission = "autoban.bypass";

	public static void onEnable(Logger log) {
		logger = log;
		logger.info("Plugin Loaded");
		logger.info("This plugin is in early alpha! Please report any bugs!");
	}
	
	public static void onDisable(Logger logger) {
		logger.info("Plugin Unloaded");
	}
	
}
