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

public class Submit extends JavaPlugin {
	
	public static Permission permission = null;
	
	@Override
	public void onEnable() {
	
		saveDefaultConfig();
		
		saveConfig();
		
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration( net.milkbowl.vault.permission.Permission.class );
        if(permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        
		getCommand("submit").setExecutor(this);
		
	}
	
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if(command.getName().equalsIgnoreCase("submit")) {
			Player player = null;
			
			if(sender instanceof Player) {
				player = (Player) sender;
			}
			
			if(args.length > 0) {
				
				if(args[0].equals("list")) {
					if(!permission.playerHas(player, "submit.list") && !player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
					
					sender.sendMessage(ChatColor.WHITE + " --- Submissions --- ");
					int i = 0;
					
					try {
						for(String key : getConfig().getConfigurationSection("submissions").getKeys(false)) {
							i++;
							sender.sendMessage(i + ". " + key);
						}
						
					} catch(Exception e) {
						sender.sendMessage("Couldn't find any results!");
					}
					
					return true;
					
					
				} else if(args[0].equals("clear")) {
					if(!permission.playerHas(player, "submit.clear") && !player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
					
					getConfig().set("submissions", null);
					
					saveConfig();
					
					sender.sendMessage(ChatColor.GRAY + "List cleared.");
					
					return true;
					
				} else if(args[0].equals("open")) {
					if(!permission.playerHas(player, "submit.open") && !player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
					
					getConfig().set("open", true);
					
					saveConfig();
					
					sender.sendMessage(ChatColor.GRAY + "You have "+ChatColor.BOLD+"opened"+ChatColor.RESET+" submissions");
					
					return true;
					
				} else if(args[0].equals("close")) {
					
					if(!permission.playerHas(player, "submit.open") && !player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
					
					getConfig().set("open", false);
					
					saveConfig();
					
					sender.sendMessage(ChatColor.GRAY + "You have "+ChatColor.BOLD+"closed"+ChatColor.RESET+" submissions.");
					
					return true;
					
				} else if(args[0].equals("view")) {
				
					if(!permission.playerHas(player, "submit.view") && !player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
					
					if(!(args.length > 1)) {
						sender.sendMessage(ChatColor.GRAY + "You need to specify a player!");

						return true;
					}
					
					if(getConfig().getString("submissions."+args[1].toLowerCase()+".l") == null) {
						sender.sendMessage(ChatColor.GRAY + "This player has not made a submission.");

						return true;
					}
					
					String[] locationData = getConfig().getString("submissions."+args[1].toLowerCase()+".l").split(" ");
					
					
						
					sender.sendMessage(ChatColor.WHITE + "Teleporting you to "+ChatColor.BOLD+args[1]+ChatColor.RESET+"'s submission.");
					
					Location l = new Location(Bukkit.getWorld(locationData[5]), Double.valueOf(locationData[0]), Double.valueOf(locationData[1]), Double.valueOf(locationData[2]));
					
					l.setPitch(Float.valueOf(locationData[3]));
					l.setYaw(Float.valueOf(locationData[4]));
					
					player.teleport(l);
					
					return true;
				}
				
			} else {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only players can run this command!");
					return true;
				}
				
				if(!permission.playerHas(player, "submit.use") && !player.isOp()) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
					return true;
				}
				
				
				if(getConfig().getBoolean("open")) {
					
					String data = 	player.getLocation().getX() + " " +
									player.getLocation().getY() + " " +
									player.getLocation().getZ() + " " +
									player.getLocation().getPitch() + " " +
									player.getLocation().getYaw() + " " +
									player.getLocation().getWorld().getName();
					
					getConfig().set("submissions."+player.getName().toLowerCase() + ".l", data);
					
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