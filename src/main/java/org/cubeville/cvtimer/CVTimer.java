package org.cubeville.cvtimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CVTimer extends JavaPlugin implements CommandExecutor {

    private Logger logger;
    private HashMap<UUID, Integer> savedTimes;
    private HashMap<UUID, Integer> startTimes;

    public void onEnable() {
        this.logger = getLogger();
        this.savedTimes = new HashMap<>();
        this.startTimes = new HashMap<>();

        logger.info(ChatColor.LIGHT_PURPLE + "Plugin Enabled Successfully");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, String[] args) {
        if(args.length > 3 || args.length < 2) {
            sendHelpCommands(sender);
        } else {
            if(args[0].equalsIgnoreCase("start")) {
                UUID pUUID = getPlayerUUID(args[1]);
                if(pUUID != null) {
                    if(!startTimes.containsKey(pUUID)) {
                        if(args.length == 3) {
                            ChatColor color = ChatColor.valueOf(args[2]);
                            //TODO START TIMER WITH COLOR/ACTIONBAR;
                        } else {
                            //TODO START TIMER
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + args[1] + " currently has a timer running! Cannot start their timer again!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
                }
            } else if(args[0].equalsIgnoreCase("stop")) {
                if(args.length == 2) {
                    UUID pUUID = getPlayerUUID(args[1]);
                    if(pUUID != null) {
                        if(startTimes.containsKey(pUUID)) {
                            //TODO STOP TIMER AND DISPLAY
                        } else {
                            sender.sendMessage(ChatColor.RED + args[1] + " currently has no timer running! Cannot stop what hasn't been started!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
                    }
                } else {
                    sendHelpCommands(sender);
                }
            } else if(args[0].equalsIgnoreCase("display")) {
                if(args.length == 2) {
                    UUID pUUID = getPlayerUUID(args[1]);
                    if(pUUID != null) {
                        if(!startTimes.containsKey(pUUID)) {
                            if(savedTimes.containsKey(pUUID)) {
                                sender.sendMessage(ChatColor.GREEN + args[1] + " has a saved time of " + savedTimes.get(pUUID)); //TODO FORMATTING
                            } else {
                                sender.sendMessage(ChatColor.RED + args[1] + " doesn't have a time saved!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + args[1] + " currently has a timer running! Cannot display time yet!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
                    }
                } else {
                    sendHelpCommands(sender);
                }
            } else {
                sendHelpCommands(sender);
            }
        }
        return true;
    }

    public void sendHelpCommands(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "-----CVTimer Commands-----");
        sender.sendMessage(ChatColor.BLUE + " - " + ChatColor.GOLD + "/timer start <player> [actionbar color (if left blank, no actionbar will be displayed)]");
        sender.sendMessage(ChatColor.BLUE + " - " + ChatColor.GOLD + "/timer stop <player>");
        sender.sendMessage(ChatColor.BLUE + " - " + ChatColor.GOLD + "/timer display <player>");
    }

    public UUID getPlayerUUID(String player) {
        if(Bukkit.getPlayer(player) == null) {
            return null;
        }
        return Bukkit.getPlayer(player).getUniqueId();
    }
}
