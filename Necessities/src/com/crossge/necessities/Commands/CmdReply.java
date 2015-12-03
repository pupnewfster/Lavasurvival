package com.crossge.necessities.Commands;

import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdReply extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a reply to send.");
            return true;
        }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User self = um.getUser(p.getUniqueId());
            if (self.isMuted()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are muted.");
                return true;
            }
            if (self.getLastC() == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You have not messaged anyone yet.");
                return true;
            }
            if (self.getLastC().equals("Console")) {
                String message = "";
                for (String arg : args)
                    message += arg + " ";
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
            User u = um.getUser(UUID.fromString(self.getLastC()));
            Player t = Bukkit.getPlayer(u.getUUID());
            if (t == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player is not online.");
                return true;
            }
            if (u.isIgnoring(p.getUniqueId())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That user is ignoring you, so you cannot reply.");
                return true;
            }
            if (self.isIgnoring(u.getUUID())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are ignoring that user, so cannot message them.");
                return true;
            }
            String message = "";
            for (String arg : args)
                message += arg + " ";
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
            if (console.getLastContact() == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You have not messaged anyone yet.");
                return true;
            }
            User u = um.getUser(console.getLastContact());
            Player t = Bukkit.getPlayer(u.getUUID());
            String message = "";
            for (String arg : args)
                message += arg + " ";
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