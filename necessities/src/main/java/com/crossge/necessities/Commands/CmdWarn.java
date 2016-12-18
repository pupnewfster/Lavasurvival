package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdWarn implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length < 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to warn and a reason.");
            return true;
        }
        UUID uuid = Necessities.getUUID().getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player target = Bukkit.getPlayer(uuid);
        String name = (sender instanceof Player ? sender.getName() : Necessities.getConsole().getName().replaceAll(":", ""));
        if (sender instanceof Player && target.hasPermission("Necessities.antiPWarn")) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not warn someone who has Necessities.antiPWarn.");
            return true;
        }
        String reason = "";
        for (int i = 1; i < args.length; i++)
            reason += args[i] + " ";
        reason = reason.trim();
        if (sender instanceof Player && sender.hasPermission("Necessities.colorchat"))
            reason = ChatColor.translateAlternateColorCodes('&', (sender.hasPermission("Necessities.magicchat") ? reason : reason.replaceAll("&k", "")));
        else if (!(sender instanceof Player))
            reason = ChatColor.translateAlternateColorCodes('&', reason);
        Necessities.getWarns().warn(target.getUniqueId(), reason, name);
        return true;
    }
}