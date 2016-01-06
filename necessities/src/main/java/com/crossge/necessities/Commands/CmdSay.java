package com.crossge.necessities.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSay extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Must enter a message to send.");
            return true;
        }
        String message = "";
        for (String arg : args)
            message += arg + " ";
        if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message.trim())).equals("")) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Must enter a message to send.");
            return true;
        }
        Bukkit.broadcastMessage((sender instanceof Player ? "" : console.getName() + ChatColor.WHITE + " ") + ChatColor.translateAlternateColorCodes('&', message.trim()));
        return true;
    }
}