package fr.mrsheepsheep.tinthealth;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TintHealth extends JavaPlugin implements Listener {

	protected TintHealth plugin;
	protected THFunctions functions;
	protected boolean fade = false;
	protected int fadetime;
	protected int intensity = 1;

	public void onEnable(){
		plugin = this;
		new PlayerListener(this);
		functions = new THFunctions(this);
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("options.fade-enabled", false);
		config.addDefault("options.fade-time", 5);
		config.addDefault("options.intensity-modifier", 1);

		saveConfig();

		fade = config.getBoolean("options.fade-enabled");
		fadetime = config.getInt("options.fade-time");
		intensity = config.getInt("options.intensity-modifier");

	}
	
	protected void smallDelay(Runnable run){
		BukkitScheduler bs = getServer().getScheduler();
		bs.scheduleSyncDelayedTask(this, run, 1L);
	}
}
