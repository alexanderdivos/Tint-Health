package fr.mrsheepsheep.tinthealth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

	TintHealth plugin;
	protected PlayerListener(TintHealth plugin){
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
	protected void onDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if (plugin.functions.isTintEnabled(p)){
				int health = plugin.functions.getPlayerHealth(p);
				if (health <= plugin.minhearts){
					int maxhealth = plugin.functions.getMaxPlayerHealth(p);
					int percentage;

					if (plugin.damagemode){
						health = (int) e.getDamage();
						percentage = 100 - (health*100) / maxhealth;
					}
					else
					{
						health = (int) (health - e.getDamage());
						percentage = (health*100) / maxhealth;
					}

					plugin.functions.sendBorder(p, percentage);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	protected void onJoin(PlayerJoinEvent e){
		if (!plugin.fade){
			final Player p = e.getPlayer();
			if (plugin.functions.isTintEnabled(p)){
				final int percentage = plugin.functions.getPlayerHealthPercentage(p);
				Runnable run = new Runnable() {
					@Override
					public void run() {
						plugin.functions.sendBorder(p, percentage);
					}
				};
				plugin.smallDelay(run);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	protected void onTeleport(PlayerTeleportEvent e){
		if (!plugin.fade){
			final Player p = e.getPlayer();
			if (plugin.functions.isTintEnabled(p)){
				if (plugin.functions.getPlayerHealth(p) <= plugin.minhearts){
					final int percentage = plugin.functions.getPlayerHealthPercentage(p);
					Runnable run = new Runnable() {
						@Override
						public void run() {
							plugin.functions.sendBorder(p, percentage);
						}
					};
					plugin.smallDelay(run);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	protected void onHeal(EntityRegainHealthEvent e){
		if (!plugin.fade){
			if (e.getEntity() instanceof Player){
				Player p = (Player) e.getEntity();
				if (plugin.functions.isTintEnabled(p)){
					Damageable d = (Damageable) p;
					double heal = e.getAmount();
					int health = (int) (d.getHealth() + heal);
					plugin.functions.sendBorder(p, health);
				}
			}
		}
	}
}
