package fr.mrsheepsheep.tinthealth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class TintHealth extends JavaPlugin implements Listener {

	protected static TintHealth plugin;
	protected THFunctions functions;
	protected boolean fade = false;
	protected int fadetime = 5;
	protected int intensity = 1;
	protected int minhearts = -1;
	protected boolean minmode = true;
	protected boolean wg = false;
	protected boolean damagemode = false;

	public void onEnable(){
		plugin = this;

		loadConfig();

		new PlayerListener(this);

		functions = new THFunctions(this);
		
		loadPlayerToggles();
		
		getCommand("tinthealth").setExecutor(new THCommand(this));

		Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
		if (wg != null && wg instanceof WorldGuardPlugin){
			this.wg = true;
			plugin.getLogger().info("WorldGuard support enabled");
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
		
		config.addDefault("damage-mode.enabled", false);

		fade = config.getBoolean("options.fade-enabled");
		fadetime = config.getInt("options.fade-time");
		intensity = config.getInt("options.intensity-modifier");
		damagemode = config.getBoolean("damage-mode.enabled");
		minhearts = config.getInt("options.minimum-health");

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
}
