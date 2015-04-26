package fr.mrsheepsheep.tinthealth;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.WorldBorderAction;

public class THFunctions {
	TintHealth plugin;
	protected ProtocolManager protocolManager;
	
	protected THFunctions(TintHealth plugin) {
		this.plugin = plugin;
		protocolManager = ProtocolLibrary.getProtocolManager();
	}

	protected void sendBorder(final Player p, int health){
		health = Math.round(health / plugin.intensity);
		setBorder(p, health);
		if (plugin.fade)
			fadeBorder(p, health, plugin.fadetime);
	}

	protected void fadeBorder(final Player p, int health, int time){
		int dist = -50000 * health + 1000000;
		sendWorldBorderPacket(p, 0, 200000D, (double) dist, (long) 1000 * time);
	}

	protected void removeBorder(Player p) {
		sendWorldBorderPacket(p, 0, 200000D, 200000D, 0);
	}

	protected void setBorder(Player p, int health){
		int dist = -50000 * health + 1000000;
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
}
