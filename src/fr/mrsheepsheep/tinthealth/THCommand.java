package fr.mrsheepsheep.tinthealth;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class THCommand implements CommandExecutor {

	TintHealth plugin;
	String heart = "\u2764 ";

	public THCommand(TintHealth plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (args.length == 1){
			switch (args[0]){
			case "reload":
				if (sender.hasPermission("tinthealth.reload")){
					plugin.reloadConfig();
					plugin.loadConfig();
					sender.sendMessage(ChatColor.GREEN + "TintHealth configuration reloaded");
				}
				else
				{
					String prefix = ChatColor.DARK_RED + heart + ChatColor.RED;
					sender.sendMessage(prefix + "You don't have permission.");
				}
				break;
			case "toggle":
				if (sender instanceof Player){
					String prefix = ChatColor.DARK_RED + heart + ChatColor.RED;
					Player p = (Player) sender;
					if (p.hasPermission("tinthealth.toggle")){
						plugin.functions.togglePlayerTint(p);
						if (plugin.functions.isTintEnabled(p))
							p.sendMessage(prefix + "Tint is now " + ChatColor.GREEN + "enabled" + ChatColor.RED + ".");
						else
							p.sendMessage(prefix + "Tint is now " + ChatColor.GRAY + "disabled" + ChatColor.RED + ".");
					}
					else
						p.sendMessage(prefix + "You don't have permission.");
				}
				else
					sender.sendMessage("This is an in-game only command !");
				break;
			default:
				sendHelpMessage(sender);
				break;
			}
			return true;
		}
		sendHelpMessage(sender);
		return true;
	}

	public void sendHelpMessage(CommandSender sender){
		String prefix = "[TintHealth] ";
		if (sender instanceof Player)
			prefix = ChatColor.DARK_RED + heart + ChatColor.GRAY;

		sender.sendMessage(prefix + ChatColor.RED + "TintHealth v" + ChatColor.GRAY + plugin.getDescription().getVersion() + ChatColor.RED + " by " + ChatColor.GRAY + "MrSheepSheep " + prefix);
		sender.sendMessage(prefix + ChatColor.YELLOW + "" + ChatColor.BOLD + "Available commands:");
		if (sender.hasPermission("tinthealth.reload"))
			sender.sendMessage(prefix + "/tint reload" + ChatColor.RED + " - Reloads the config");
		if (sender.hasPermission("tinthealth.toggle"))
			sender.sendMessage(prefix + "/tint toggle" + ChatColor.RED + " - Enable / Disable the tint for you");
		if (!sender.hasPermission("tinthealth.toggle") && !sender.hasPermission("tinthealth.reload"))
			sender.sendMessage(prefix + "None !");
	}

}
