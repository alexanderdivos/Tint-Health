package fr.mrsheepsheep.tinthealth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

	@EventHandler
	protected void onDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			double dmg = e.getFinalDamage();
			Damageable d = (Damageable) p;
			int health = (int) (d.getHealth() - dmg);
			plugin.functions.sendBorder(p, health);
		}
	}

	@EventHandler
	protected void onJoin(PlayerJoinEvent e){
		if (!plugin.fade){
			final Player p = e.getPlayer();
			Damageable d = (Damageable) p;
			final double health = d.getHealth();
			Runnable run = new Runnable() {

				@Override
				public void run() {
					plugin.functions.sendBorder(p, (int) health);
				}
			};
			plugin.smallDelay(run);
		}
	}

	@EventHandler
	protected void onTeleport(PlayerTeleportEvent e){
		if (!plugin.fade){
			final Player p = e.getPlayer();
			Damageable d = (Damageable) p;
			final double health = d.getHealth();
			Runnable run = new Runnable() {
				@Override
				public void run() {
					plugin.functions.sendBorder(p, (int) health);
				}
			};
			plugin.smallDelay(run);
		}
	}

	@EventHandler
	protected void onHeal(EntityRegainHealthEvent e){
		if (!plugin.fade){
			if (e.getEntity() instanceof Player){
				Player p = (Player) e.getEntity();
				Damageable d = (Damageable) p;
				double heal = e.getAmount();
				int health = (int) (d.getHealth() + heal);
				plugin.functions.sendBorder(p, health);
			}
		}
	}
}
