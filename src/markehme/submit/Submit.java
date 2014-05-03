package markehme.submit;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *  Submit: A simple plugin to submit the current location.
 *  Copyright (C) 2014   MarkehMe / Mark Hughes <mark@markeh.me>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
public class Submit extends JavaPlugin {
	
	public static Permission permission = null;
	
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		
		saveConfig();
		
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if(permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        
		getCommand("submit").setExecutor(this);
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("submit")) {
			Player player = null;
			
			if(sender instanceof Player) {
				player = (Player) sender;
			}
			
			String worldIdentifier = null;
			
			if(getConfig().getBoolean("separateByWorld")) {
				worldIdentifier = player.getWorld().getName().toLowerCase();
			} else {
				worldIdentifier = "__Submit__.__Global__";
			}
			
			if(args.length > 0) {
				if(args[0].equals("list")) {
					if(sender instanceof Player) { 
						if(!permission.playerHas(player, "submit.list") && !sender.isOp()) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
							return true;
						}
					}
					
					sender.sendMessage(ChatColor.AQUA + " --- "+ChatColor.BOLD+"Submissions"+ChatColor.RESET+ChatColor.AQUA+" --- ");
					int i = 0;
					
					try {
						for(String key : getConfig().getConfigurationSection("submissions."+worldIdentifier).getKeys(false)) {
							i++;
							sender.sendMessage(i + ". " + key);
						}
						
					} catch(Exception e) {
						sender.sendMessage("Couldn't find any results!");
					}
					
					return true;
					
				} else if(args[0].equals("clear")) {
					
					if(sender instanceof Player) { 
						if(!permission.playerHas(player, "submit.clear") && !sender.isOp()) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
							return true;
						}
					}
					
					getConfig().set("submissions."+worldIdentifier, null);
					
					saveConfig();
					
					sender.sendMessage(ChatColor.GRAY + "List cleared.");
					
					return true;
					
				} else if(args[0].equals("open")) {
					
					if(sender instanceof Player) { 
						if(!permission.playerHas(player, "submit.open") && !sender.isOp()) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
							return true;
						}
					}
					
					getConfig().set("open", true);
					
					saveConfig();
					
					sender.sendMessage(ChatColor.GRAY + "You have "+ChatColor.BOLD+"opened"+ChatColor.RESET+""+ChatColor.GRAY+" submissions");
					
					return true;
					
				} else if(args[0].equals("close")) {
					
					if(sender instanceof Player) { 
						if(!permission.playerHas(player, "submit.open") && !sender.isOp()) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
							return true;
						}
					}
					
					getConfig().set("open", false);
					
					saveConfig();
					
					sender.sendMessage(ChatColor.GRAY + "You have "+ChatColor.BOLD+"closed"+ChatColor.RESET+""+ChatColor.GRAY+" submissions.");
					
					return true;
					
				} else if(args[0].equals("view")) {
				
					if(!(sender instanceof Player)) {
						sender.sendMessage("You can't view submissions from console.");
						return true;
					}
					
					if(!permission.playerHas(player, "submit.view") && !player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
					
					if(!(args.length > 1)) {
						sender.sendMessage(ChatColor.GRAY + "You need to specify a player!");

						return true;
					}
					
					if(getConfig().getString("submissions."+worldIdentifier+"."+args[1].toLowerCase()+".l") == null) {
						sender.sendMessage(ChatColor.GRAY + "This player has not made a submission.");

						return true;
					}
					
					String[] locationData = getConfig().getString("submissions."+worldIdentifier+"."+args[1].toLowerCase()+".l").split(" ");
					
					sender.sendMessage(ChatColor.WHITE + "Teleporting you to "+ChatColor.BOLD+args[1]+ChatColor.WHITE+"'s submission.");
					
					Location l = new Location(Bukkit.getWorld(locationData[5]), Double.valueOf(locationData[0]), Double.valueOf(locationData[1]), Double.valueOf(locationData[2]));
					
					l.setPitch(Float.valueOf(locationData[3]));
					l.setYaw(Float.valueOf(locationData[4]));
					
					player.teleport(l);
					
					return true;
				} else if(args[0].equalsIgnoreCase("reload")) {
					if(sender instanceof Player) {
						if(!permission.playerHas(player, "submit.reload") && !player.isOp()) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
							return true;
						}
					}
					
					reloadConfig();
					sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
					
					return true;
				}
				
			} else {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only players can make a submission.");
					return true;
				}
				
				if(sender instanceof Player) { 
					if(!permission.playerHas(player, "submit.use") && !player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
				}
				
				
				if(getConfig().getBoolean("open")) {
					
					String data = 	player.getLocation().getX() + " " +
									player.getLocation().getY() + " " +
									player.getLocation().getZ() + " " +
									player.getLocation().getPitch() + " " +
									player.getLocation().getYaw() + " " +
									player.getLocation().getWorld().getName();
					
					getConfig().set("submissions."+worldIdentifier+"."+player.getName().toLowerCase() + ".l", data);
					
					saveConfig();
					
					sender.sendMessage(ChatColor.GREEN + "Your submission has been set to your current location!");
					
				} else {
					sender.sendMessage(ChatColor.GRAY + "Submissions are not current open.");
				}
				
				return true;
			}
			
		}
		
		sender.sendMessage(ChatColor.GRAY + "Usage: /submit - Subcommands: list, clear, open, close, view.");
		
		return true;
	}
}