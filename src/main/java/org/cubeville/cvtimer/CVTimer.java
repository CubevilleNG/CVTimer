package org.cubeville.cvtimer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class CVTimer extends JavaPlugin implements CommandExecutor, Listener {

    private Logger logger;
    private BukkitScheduler scheduler;

    private HashMap<UUID, Long> savedTimes;
    private HashMap<UUID, Long> startTimes;
    private HashMap<UUID, Integer> actionbarSchedulers;
    private HashMap<UUID, String> players;

    public void onEnable() {
        this.logger = getLogger();
        this.scheduler = Bukkit.getServer().getScheduler();
        this.savedTimes = new HashMap<>();
        this.startTimes = new HashMap<>();
        this.actionbarSchedulers = new HashMap<>();
        this.players = new HashMap<>();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logger.info(ChatColor.LIGHT_PURPLE + "PlaceholderAPI found. Attempting to register variables");
            new PlaceholderAPIUtil(this).register();
            logger.info(ChatColor.LIGHT_PURPLE + "Variables registered");
        } else {
            logger.info(ChatColor.YELLOW + "PlaceholderAPI not found. Skipping variable registration");
        }

        Bukkit.getPluginManager().registerEvents(this, this);
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
                            ChatColor color;
                            try {
                                color = ChatColor.valueOf(args[2].toUpperCase());
                            } catch(IllegalArgumentException e) {
                                sender.sendMessage(ChatColor.RED + args[2] + " is an invalid color!");
                                return true;
                            }
                            startTimer(pUUID);
                            scheduleActionbar(Objects.requireNonNull(Bukkit.getPlayer(pUUID)), color);
                        } else {
                            startTimer(pUUID);
                            sender.sendMessage(ChatColor.GREEN + "Timer started for " + args[1]);
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
                            stopTimer(pUUID);
                            cancelActionbar(Objects.requireNonNull(Bukkit.getPlayer(pUUID)));
                            sender.sendMessage(ChatColor.GREEN + "Timer stopped for " + args[1] + " with a time of " + formatTime(savedTimes.get(pUUID), true));
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
                                sender.sendMessage(ChatColor.GREEN + args[1] + " has a saved time of " + formatTime(savedTimes.get(pUUID), true));
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

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        this.logoutCleanup(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        this.players.put(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    public void sendHelpCommands(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "----------CVTimer Commands----------");
        sender.sendMessage(ChatColor.BLUE + " - " + ChatColor.GOLD + "/timer start <player> [actionbar color (if left blank, no actionbar will be displayed)]");
        sender.sendMessage(ChatColor.BLUE + " - " + ChatColor.GOLD + "/timer stop <player>");
        sender.sendMessage(ChatColor.BLUE + " - " + ChatColor.GOLD + "/timer display <player>");
    }

    public void startTimer(UUID pUUID) {
        this.startTimes.put(pUUID, System.currentTimeMillis());
    }

    public void stopTimer(UUID pUUID) {
        this.savedTimes.put(pUUID, System.currentTimeMillis() - this.startTimes.remove(pUUID));
    }

    public void scheduleActionbar(Player player, ChatColor color) {
        int taskID = this.scheduler.runTaskTimer(this, () -> {
            String time = formatTime(System.currentTimeMillis() - this.startTimes.get(player.getUniqueId()), false);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color + time));
        }, 0, 20).getTaskId();
        this.actionbarSchedulers.put(player.getUniqueId(), taskID);
    }

    public void cancelActionbar(Player player) {
        if(this.actionbarSchedulers.containsKey(player.getUniqueId())) {
            this.scheduler.cancelTask(this.actionbarSchedulers.remove(player.getUniqueId()));
        }
    }

    public void logoutCleanup(Player player) {
        this.cancelActionbar(player);
        this.startTimes.remove(player.getUniqueId());
    }

    public String formatTime(Long time, boolean decimalIncluded) {
        String out = "";
        DecimalFormat dF;
        int hours;
        int minutes;
        int seconds;
        if(decimalIncluded) {
            dF = new DecimalFormat("#.##");
            int inputSeconds = Integer.parseInt(dF.format(time / 1000.0f).substring(0, dF.format(time / 1000.0f).indexOf(".")));
            hours = inputSeconds / 3600;
            minutes = (inputSeconds % 3600) / 60;
            seconds = (inputSeconds % 3600) % 60;
            if(hours > 0) out = out + hours + "h ";
            if(minutes > 0) out = out + minutes + "m ";
            if(seconds > 0) out = out + seconds + dF.format(time / 1000.0f).substring(dF.format(time / 1000.0f).indexOf(".")) + "s";
        } else {
            dF = new DecimalFormat("#");
            int inputSeconds = Integer.parseInt(dF.format(time / 1000.0f));
            hours = inputSeconds / 3600;
            minutes = (inputSeconds % 3600) / 60;
            seconds = (inputSeconds % 3600) % 60;
            if(hours > 0) out = out + hours + "h ";
            if(minutes > 0) out = out + minutes + "m ";
            if(seconds > 0) out = out + seconds + "s";
        }
        return out;
    }

    public UUID getPlayerUUID(String player) {
        for(UUID pUUID : this.players.keySet()) {
            if(this.players.get(pUUID).equalsIgnoreCase(player)) return pUUID;
        }
        return null;
    }

    public String getFinalTime(String pName, boolean formatted) {
        UUID pUUID = getPlayerUUID(pName);
        if(pUUID != null && this.savedTimes.containsKey(pUUID)) {
            if(formatted) {
                return this.formatTime(this.savedTimes.get(pUUID), true);
            }
            return String.valueOf(this.savedTimes.get(pUUID));
        }
        return String.valueOf(0);
    }

    public String getCurrentTime(String pName, boolean formatted) {
        UUID pUUID = getPlayerUUID(pName);
        if(pUUID != null && this.startTimes.containsKey(pUUID)) {
            if(formatted) {
                return this.formatTime(System.currentTimeMillis() - this.startTimes.get(pUUID), true);
            }
            return String.valueOf(System.currentTimeMillis() - this.startTimes.get(pUUID));
        }
        return String.valueOf(0);
    }
}
