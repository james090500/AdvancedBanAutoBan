package com.james090500;

import java.util.logging.Logger;

public class Main {

	public static void onEnable(Logger logger) {
		logger.info("Plugin Loaded");
		logger.info("This plugin is in early alpha! Please report any bugs!");
	}
	
	public static void onDisable(Logger logger) {
		logger.info("Plugin Unloaded");
	}
	
}
