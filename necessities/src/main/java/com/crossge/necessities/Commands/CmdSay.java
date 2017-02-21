package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSay implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Must enter a message to send.");
            return true;
        }
        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args)
            messageBuilder.append(arg).append(" ");
        String message = messageBuilder.toString();
        if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message.trim())).equals("")) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Must enter a message to send.");
            return true;
        }
        Bukkit.broadcastMessage((sender instanceof Player ? "" : Necessities.getConsole().getName() + ChatColor.WHITE + " ") + ChatColor.translateAlternateColorCodes('&', message.trim()));
        return true;
    }
}