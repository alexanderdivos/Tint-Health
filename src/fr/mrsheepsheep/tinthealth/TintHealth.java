package fr.mrsheepsheep.tinthealth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;



public class TintHealth extends JavaPlugin implements Listener {

	protected static TintHealth plugin;
	protected THFunctions functions;
	protected boolean fade = false;
	protected int fadetime = 5;
	protected int intensity = 1;
	protected int minhearts = -1;
	protected boolean minmode = true;
	protected boolean damagemode = false;
	protected boolean enabled = true;
	protected boolean debug = false;

	public void onEnable(){

		plugin = this;

	    try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	        plugin.getLogger().warning("Metrics cannot be started");
	        e.printStackTrace();
	    }
	    
		checkVersion();
		loadConfig();
		if (enabled)
			new PlayerListener(this);

		functions = new THFunctions(this);
		
		loadPlayerToggles();
		
		getCommand("tinthealth").setExecutor(new THCommand(this));
	}
	
	private void checkVersion() {
		String version = Bukkit.getBukkitVersion();
		if (!version.startsWith("1.8")){
			plugin.setEnabled(false);
			plugin.getLogger().warning(version + " is not compatible with Tint Health ! Disabling plugin.");
		}
		
	}

	public void onDisable(){
		savePlayerToggles();
	}
	
	public THAPI getAPI(){
		return new THAPI(plugin);
	}

	protected void smallDelay(Runnable run){
		BukkitScheduler bs = getServer().getScheduler();
		bs.scheduleSyncDelayedTask(this, run, 1L);
	}

	protected void loadConfig(){

		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("options.fade-enabled", false);
		config.addDefault("options.fade-time", 5);
		config.addDefault("options.intensity-modifier", 1);
		config.addDefault("options.minimum-health", -1);
		config.addDefault("options.enabled", true);
		config.addDefault("options.debug", false);
		config.addDefault("damage-mode.enabled", false);

		fade = config.getBoolean("options.fade-enabled");
		fadetime = config.getInt("options.fade-time");
		intensity = config.getInt("options.intensity-modifier");
		damagemode = config.getBoolean("damage-mode.enabled");
		minhearts = config.getInt("options.minimum-health");
		enabled = config.getBoolean("options.enabled");
		debug = config.getBoolean("options.debug");
		if (intensity < 1){
			config.set("options.intensity-modifier", 1);
			intensity = 1;
			plugin.getLogger().warning("Intensity modifier cannot be less than 1. Changing to 1.");
		}

		if (minhearts < 0){
			minhearts = 100000;
		}
		saveConfig();
		plugin.getLogger().info("Configuration successfully loaded");
	}

	protected void loadPlayerToggles(){
		File f = new File("plugins/TintHealth", "disabledPlayers.yml");
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(f);
		fc.options().copyDefaults(true);
		fc.addDefault("players", null);
		if (fc.getStringList("players") != null){
			for (String pname : fc.getStringList("players"))
				plugin.functions.togglelist.add(pname);
		}
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		plugin.getLogger().info(fc.getStringList("players").size() + " player toggles loaded");
	}

	protected void savePlayerToggles(){
		File f = new File("plugins/TintHealth", "disabledPlayers.yml");
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(f);
		List<String> list = new ArrayList<String>();
		for (String pname : plugin.functions.togglelist){
			list.add(pname);
		}
		fc.set("players", list);
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		plugin.getLogger().info(list.size() + " player toggles saved");
	}
	
	public void debug(String message){
		if (debug)
			plugin.getLogger().info("[DEBUG] " + message);
	}
}
