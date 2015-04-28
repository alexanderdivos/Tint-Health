package fr.mrsheepsheep.tinthealth;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.WorldBorderAction;

public class THFunctions {
	TintHealth plugin;
	protected ProtocolManager protocolManager;
	protected List<String> togglelist = new ArrayList<String>();

	protected THFunctions(TintHealth plugin) {
		this.plugin = plugin;
		protocolManager = ProtocolLibrary.getProtocolManager();
	}

	protected void sendBorder(Player p, int percentage){
		percentage = Math.round(percentage / plugin.intensity);
		setBorder(p, percentage);
		if (plugin.fade && p.hasPermission("tinthealth.fade"))
			fadeBorder(p, percentage, plugin.fadetime);
	}

	protected void fadeBorder(Player p, int percentage, int time){
		int dist = -10000 * percentage + 1300000;
		sendWorldBorderPacket(p, 0, 200000D, (double) dist, (long) 1000 * time + 4000); //Add 4000 to make sure the "security" zone does not count in the fade time
	}

	protected void removeBorder(Player p) {
		sendWorldBorderPacket(p, 0, 200000D, 200000D, 0);
	}

	protected void setBorder(Player p, int percentage){
		int dist = -10000 * percentage + 1300000;
		sendWorldBorderPacket(p, dist, 200000D, 200000D, 0);
	}

	protected void sendWorldBorderPacket(Player p, int dist, double oldradius, double newradius, long time){

		PacketContainer border = protocolManager.createPacket(PacketType.Play.Server.WORLD_BORDER);
		border.getWorldBorderActions().write(0, WorldBorderAction.INITIALIZE);
		border.getIntegers()
		.write(0, 29999984)
		.write(1, 15)
		.write(2, dist);
		border.getLongs()
		.write(0, time);
		border.getDoubles()
		.write(0, p.getLocation().getX())
		.write(1, p.getLocation().getY())
		.write(2, newradius)
		.write(3, oldradius);
		try {
			protocolManager.sendServerPacket(p, border);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(
					"Cannot send packet " + border, e);
		}
	}

	protected int getPlayerHealth(Player p){
		Damageable d = (Damageable) p;
		return (int) d.getHealth();
	}

	protected int getMaxPlayerHealth(Player p){
		Damageable d = (Damageable) p;
		return (int) d.getMaxHealth();
	}

	protected int getPlayerMissingHearts(Player p){
		Damageable d = (Damageable) p;
		return (int) d.getMaxHealth();
	}

	protected int getPlayerHealthPercentage(Player p){
		int health = getPlayerHealth(p);
		int maxhealth = getMaxPlayerHealth(p);
		float percentage = (health*100)/maxhealth;
		return (Math.round(percentage));
	}

	protected void disablePlayerTint(Player p){
		String pname = p.getName();
		togglelist.add(pname);
	}

	protected void enablePlayerTint(Player p){
		String pname = p.getName();
		if (togglelist.contains(pname))
			togglelist.remove(pname);
	}

	protected void togglePlayerTint(Player p){
		if (isTintEnabled(p))
			disablePlayerTint(p);
		else
			enablePlayerTint(p);
	}

	protected boolean isTintEnabled(Player p){
		String pname = p.getName();
		return !togglelist.contains(pname);
	}
}
