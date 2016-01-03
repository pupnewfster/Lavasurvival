package com.crossge.necessities.Commands;

import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class CmdNick extends Cmd {
    private File configFile = new File("plugins/Necessities", "config.yml");
    UserManager um = new UserManager();

    public boolean commandUse(CommandSender sender, String[] args) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                User u = um.getUser(p.getUniqueId());
                u.setNick(null);
                p.setDisplayName(p.getName());
                p.sendMessage(var.getMessages() + "Nickname removed.");
                return true;
            } else if (args.length == 1) {
                UUID uuid = get.getID(args[0]);
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', args[0] + "&r")).trim().length() > 24 ||
                        ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', args[0] + "&r")).trim().length() < 1) {
                    p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Nicks have a maximum of 24 characters.");
                    return true;
                }
                if (uuid == null) {
                    User u = um.getUser(p.getUniqueId());
                    String nick = args[0];
                    if (p.hasPermission("Necessities.magicchat"))
                        nick = ChatColor.translateAlternateColorCodes('&', nick);
                    else
                        nick = ChatColor.translateAlternateColorCodes('&', nick.replaceAll("&k", ""));
                    u.setNick("~" + nick.trim() + "&r");
                    p.setDisplayName("~" + ChatColor.translateAlternateColorCodes('&', nick.trim() + "&r"));
                    p.sendMessage(var.getMessages() + "Nickname set to " + p.getDisplayName());
                    return true;
                }
                Player target = sender.getServer().getPlayer(uuid);
                if (!p.hasPermission("Necessities.nickOthers"))
                    target = p;
                target.setDisplayName(target.getName());
                User u = um.getUser(target.getUniqueId());
                u.setNick(null);
                target.sendMessage(var.getMessages() + "Nickname removed.");
                p.sendMessage(var.getMessages() + "Nickname for " + var.getObj() + target.getName() + var.getMessages() + " removed");
                return true;
            }
        } else if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console cannot have a nickname.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player target = sender.getServer().getPlayer(uuid);
        if (sender instanceof Player && !sender.hasPermission("Necessities.nickOthers"))
            target = ((Player) sender);
        if (args.length == 1) {
            target.setDisplayName(target.getName());
            User u = um.getUser(target.getUniqueId());
            u.setNick(null);
            target.sendMessage(var.getMessages() + "Nickname removed.");
            sender.sendMessage(var.getMessages() + "Nickname for " + var.getObj() + target.getName() + var.getMessages() + " removed");
        } else {
            User u = um.getUser(target.getUniqueId());
            if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', args[1] + "&r")).trim().length() > 24 ||
                    ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', args[1] + "&r")).trim().length() < 1) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Nicks have a maximum of 24 characters.");
                return true;
            }
            u.setNick("~" + args[1].trim() + "&r");
            target.setDisplayName("~" + ChatColor.translateAlternateColorCodes('&', args[1] + "&r").trim());
            target.sendMessage(var.getMessages() + "Nickname set to " + target.getDisplayName());
            sender.sendMessage(var.getMessages() + "Nickname for " + var.getObj() + target.getName() + var.getMessages() + " set to " + target.getDisplayName());
        }
        return true;
    }
}