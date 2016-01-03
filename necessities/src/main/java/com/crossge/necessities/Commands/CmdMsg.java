package com.crossge.necessities.Commands;

import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdMsg extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to message and a message to send.");
            return true;
        }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User self = um.getUser(p.getUniqueId());
            if (self.isMuted()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are muted.");
                return true;
            }
            if (args[0].equalsIgnoreCase("console")) {
                String message = "";
                for (int i = 1; i < args.length; i++)
                    message += args[i] + " ";
                message = ChatColor.WHITE + message.trim();
                if (p.hasPermission("Necessities.colorchat")) {
                    if (p.hasPermission("Necessities.magicchat"))
                        message = ChatColor.translateAlternateColorCodes('&', message);
                    else
                        message = ChatColor.translateAlternateColorCodes('&', message.replaceAll("&k", ""));
                }
                self.setLastC("Console");
                console.setLastContact(self.getUUID());
                p.sendMessage(var.getMessages() + "[me -> " + console.getName().replaceAll(":", "") + "] " + message);
                Bukkit.getConsoleSender().sendMessage(var.getMessages() + "[" + p.getDisplayName() + var.getMessages() + " -> me] " + message);
                return true;
            }
            UUID uuid = get.getID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            User u = um.getUser(uuid);
            if (u.isIgnoring(p.getUniqueId())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That user is ignoring you, so you cannot message them.");
                return true;
            }
            if (self.isIgnoring(uuid)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are ignoring that user, so cannot message them.");
                return true;
            }
            Player t = Bukkit.getPlayer(uuid);
            String message = "";
            for (int i = 1; i < args.length; i++)
                message += args[i] + " ";
            message = ChatColor.WHITE + message.trim();
            if (p.hasPermission("Necessities.colorchat")) {
                if (p.hasPermission("Necessities.magicchat"))
                    message = ChatColor.translateAlternateColorCodes('&', message);
                else
                    message = ChatColor.translateAlternateColorCodes('&', message.replaceAll("&k", ""));
            }
            u.setLastC(self.getUUID().toString());
            self.setLastC(u.getUUID().toString());
            p.sendMessage(var.getMessages() + "[me -> " + t.getDisplayName() + var.getMessages() + "] " + message);
            t.sendMessage(var.getMessages() + "[" + p.getDisplayName() + var.getMessages() + " -> me] " + message);
        } else {
            UUID uuid = get.getID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            User u = um.getUser(uuid);
            Player t = Bukkit.getPlayer(uuid);
            String message = "";
            for (int i = 1; i < args.length; i++)
                message += args[i] + " ";
            message = ChatColor.WHITE + message.trim();
            message = ChatColor.translateAlternateColorCodes('&', message);
            u.setLastC("Console");
            console.setLastContact(u.getUUID());
            sender.sendMessage(var.getMessages() + "[me -> " + t.getDisplayName() + var.getMessages() + "] " + message);
            t.sendMessage(var.getMessages() + "[" + console.getName().replaceAll(":", "") + " -> me] " + message);
        }
        return true;
    }
}