package fr.mrsheepsheep.tinthealth;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

public class THFunctions {
	
	private static Method handle, sendPacket;
	private static Method center, distance, time, movement;
	private static Field player_connection;
	private static Constructor<?> constructor, border_constructor;
	private static Object constant;
	
	static {
	    try {
			handle = getClass("org.bukkit.craftbukkit", "entity.CraftPlayer").getMethod("getHandle");
			player_connection = getClass("net.minecraft.server", "EntityPlayer").getField("playerConnection");
			for (Method m : getClass("net.minecraft.server", "PlayerConnection").getMethods()) {
				if (m.getName().equals("sendPacket")) {
					sendPacket = m;
					break;
				}
			}
			Class<?> enumclass;
			try {
				enumclass = getClass("net.minecraft.server", "EnumWorldBorderAction");
			} catch(ClassNotFoundException x) {
				enumclass = getClass("net.minecraft.server", "PacketPlayOutWorldBorder$EnumWorldBorderAction");
			}
			constructor = getClass("net.minecraft.server", "PacketPlayOutWorldBorder").getConstructor(getClass("net.minecraft.server", "WorldBorder"), enumclass);
			border_constructor = getClass("net.minecraft.server", "WorldBorder").getConstructor();
			
			Method[] methods = getClass("net.minecraft.server", "WorldBorder").getMethods();

			String setCenter = "setCenter";
			String setWarningDistance = "setWarningDistance";
			String setWarningTime = "setWarningTime";
			String transitionSizeBetween = "transitionSizeBetween";
			
			if (!inClass(methods, setCenter))
				setCenter = "c";
			if (!inClass(methods, setWarningDistance))
				setWarningDistance = "c";
			if (!inClass(methods, setWarningTime))
				setWarningTime = "b";
			if (!inClass(methods, transitionSizeBetween))
				transitionSizeBetween = "a";
			
			center = getClass("net.minecraft.server", "WorldBorder").getMethod(setCenter, double.class, double.class);
			distance = getClass("net.minecraft.server", "WorldBorder").getMethod(setWarningDistance, int.class);
			time = getClass("net.minecraft.server", "WorldBorder").getMethod(setWarningTime, int.class);
			movement = getClass("net.minecraft.server", "WorldBorder").getMethod(transitionSizeBetween, double.class, double.class, long.class);
			
			for (Object o: enumclass.getEnumConstants()) {
				if (o.toString().equals("INITIALIZE")) {
					constant = o;
					break;
				}
			}
	    } catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private static boolean inClass(Method[] methods, String methodName){
		for (Method m : methods)
			if (m.getName() == methodName)
				return true;
		return false;
	}
	
	private static Class<?> getClass(String prefix, String name) throws Exception {
		return Class.forName(new StringBuilder().append(prefix + ".").append(Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1)).append(".").append(name).toString());
	}
	
	TintHealth plugin;
	protected List<String> togglelist = new ArrayList<String>();

	protected THFunctions(TintHealth plugin) {
		this.plugin = plugin;
	}

	protected void sendBorder(Player p, int percentage){
		percentage = Math.round(percentage / plugin.intensity);
		setBorder(p, percentage);
		if (plugin.fade && p.hasPermission("tinthealth.fade")) fadeBorder(p, percentage, plugin.fadetime);
	}

	protected void fadeBorder(Player p, int percentage, long time){
		int dist = -10000 * percentage + 1300000;
		sendWorldBorderPacket(p, 0, 200000D, (double) dist, (long) 1000 * time + 4000); //Add 4000 to make sure the "security" zone does not count in the fade time
		plugin.debug("Sent fade border for player " + p.getName());
	}

	protected void removeBorder(Player p) {
		sendWorldBorderPacket(p, 0, 200000D, 200000D, 0);
		plugin.debug("Removed tint for player " + p.getName());
	}

	protected void setBorder(Player p, int percentage){
		int dist = -10000 * percentage + 1300000;
		sendWorldBorderPacket(p, dist, 200000D, 200000D, 0);
		plugin.debug("Set " + percentage + "% tint for player " + p.getName());
	}

	protected void sendWorldBorderPacket(Player p, int dist, double oldradius, double newradius, long delay) {
//		
//			New Reflection Version
//		
		try {
			Object worldborder = border_constructor.newInstance();
			center.invoke(worldborder, p.getLocation().getX(), p.getLocation().getY());
			distance.invoke(worldborder, dist);
			time.invoke(worldborder, 15);
			movement.invoke(worldborder, oldradius, newradius, delay);
			
			Object packet = constructor.newInstance(worldborder, constant);
			sendPacket.invoke(player_connection.get(handle.invoke(p)), packet);
		} catch(Exception x) {
			x.printStackTrace();
		}		
//			Old ProtocollLib Version:
//		
//		border.getWorldBorderActions().write(0, WorldBorderAction.INITIALIZE);
//		border.getIntegers()
//		.write(0, 29999984)
//		.write(1, 15)
//		.write(2, dist);
//		border.getLongs()
//		.write(0, time);
//		border.getDoubles()
//		.write(0, p.getLocation().getX())
//		.write(1, p.getLocation().getY())
//		.write(2, newradius)
//		.write(3, oldradius);
//		try {
//			protocolManager.sendServerPacket(p, border);
//		} catch (InvocationTargetException e) {
//			throw new RuntimeException("Cannot send packet " + border, e);
//		}
	}

	protected int getPlayerHealth(Player p){
		return (int) ((Damageable) p).getHealth();
	}

	protected int getMaxPlayerHealth(Player p){
		return (int) ((Damageable) p).getMaxHealth();
	}

	protected int getPlayerMissingHearts(Player p){
		return (int) ((Damageable) p).getMaxHealth();
	}

	protected int getPlayerHealthPercentage(Player p){
		int health = getPlayerHealth(p);
		int maxhealth = getMaxPlayerHealth(p);
		return (Math.round((health * 100) / maxhealth));
	}

	protected void disablePlayerTint(Player p){
		togglelist.add(p.getName());
	}

	protected void enablePlayerTint(Player p){
		String pname = p.getName();
		if (togglelist.contains(pname)) togglelist.remove(pname);
	}

	protected void togglePlayerTint(Player p){
		if (isTintEnabled(p)) disablePlayerTint(p);
		else enablePlayerTint(p);
	}

	protected boolean isTintEnabled(Player p) {
		return !togglelist.contains(p.getName());
	}
}
