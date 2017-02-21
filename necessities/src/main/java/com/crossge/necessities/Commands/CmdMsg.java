package com.crossge.necessities.Commands;

import com.crossge.necessities.Console;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdMsg implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length < 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to message and a message to send.");
            return true;
        }
        Console console = Necessities.getConsole();
        UserManager um = Necessities.getUM();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User self = um.getUser(p.getUniqueId());
            if (self.isMuted()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are muted.");
                return true;
            }
            if (args[0].equalsIgnoreCase("console")) {
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                    messageBuilder.append(args[i]).append(" ");
                String message = ChatColor.WHITE + messageBuilder.toString().trim();
                if (p.hasPermission("Necessities.colorchat"))
                    message = ChatColor.translateAlternateColorCodes('&', (p.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", "")));
                self.setLastC("Console");
                console.setLastContact(self.getUUID());
                p.sendMessage(var.getMessages() + "[me -> " + console.getName().replaceAll(":", "") + "] " + message);
                Bukkit.getConsoleSender().sendMessage(var.getMessages() + "[" + p.getDisplayName() + var.getMessages() + " -> me] " + message);
                return true;
            }
            UUID uuid = Utils.getID(args[0]);
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
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                messageBuilder.append(args[i]).append(" ");
            String message = ChatColor.WHITE + messageBuilder.toString().trim();
            if (p.hasPermission("Necessities.colorchat"))
                message = ChatColor.translateAlternateColorCodes('&', (p.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", "")));
            u.setLastC(self.getUUID().toString());
            self.setLastC(u.getUUID().toString());
            p.sendMessage(var.getMessages() + "[me -> " + t.getDisplayName() + var.getMessages() + "] " + message);
            t.sendMessage(var.getMessages() + "[" + p.getDisplayName() + var.getMessages() + " -> me] " + message);
        } else {
            UUID uuid = Utils.getID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            User u = um.getUser(uuid);
            Player t = Bukkit.getPlayer(uuid);
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                messageBuilder.append(args[i]).append(" ");
            String message = ChatColor.translateAlternateColorCodes('&', ChatColor.WHITE + messageBuilder.toString().trim());
            u.setLastC("Console");
            console.setLastContact(u.getUUID());
            sender.sendMessage(var.getMessages() + "[me -> " + t.getDisplayName() + var.getMessages() + "] " + message);
            t.sendMessage(var.getMessages() + "[" + console.getName().replaceAll(":", "") + " -> me] " + message);
        }
        return true;
    }
}