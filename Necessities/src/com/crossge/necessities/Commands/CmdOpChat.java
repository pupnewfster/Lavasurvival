package com.crossge.necessities.Commands;

import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class CmdOpChat extends Cmd {
    private File configFile = new File("plugins/Necessities", "config.yml");

    public boolean commandUse(CommandSender sender, String[] args) {
        String message = "";
        if (args.length > 0)
            for (String arg : args)
                message += arg + " ";
        message = message.trim();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = um.getUser(p.getUniqueId());
            if (args.length > 0)
                sendOps(p.getUniqueId(), message);
            else if (!u.opChat()) {
                p.sendMessage(var.getMessages() + "You are now sending messages only to ops.");
                u.toggleOpChat();
            } else {
                p.sendMessage(var.getMessages() + "You are no longer sending messages to ops.");
                u.toggleOpChat();
            }
        } else {
            if (args.length == 0)
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console cannot toggle opchat.");
            else
                consoleToOps(message);
        }
        return true;
    }

    private void sendOps(UUID uuid, String message) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        Player player = Bukkit.getPlayer(uuid);
        String send = ChatColor.translateAlternateColorCodes('&', config.getString("Necessities.ChatFormat"));
        send = var.getMessages() + "To Ops - " + ChatColor.WHITE + send;
        send = send.replaceAll("\\{TITLE\\} ", "");
        send = send.replaceAll("\\{RANK\\}", ChatColor.translateAlternateColorCodes('&', um.getUser(uuid).getRank().getTitle()));
        send = send.replaceAll("\\{NAME\\}", player.getDisplayName());
        send = send.replaceAll("\\{MESSAGE\\}", "");
        if (player.hasPermission("Necessities.colorchat")) {
            if (player.hasPermission("Necessities.magicchat"))
                message = ChatColor.translateAlternateColorCodes('&', message);
            else
                message = ChatColor.translateAlternateColorCodes('&', message.replaceAll("&k", ""));
        }
        Bukkit.broadcast(send + message, "Necessities.opBroadcast");
        Bukkit.getConsoleSender().sendMessage(send + message);
    }

    private void consoleToOps(String message) {
        String send = var.getMessages() + "To Ops - " + console.getName() + ChatColor.WHITE + " " +
                ChatColor.translateAlternateColorCodes('&', message.trim());
        Bukkit.broadcast(send, "Necessities.opBroadcast");
        Bukkit.getConsoleSender().sendMessage(send);
    }
}