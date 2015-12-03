package com.crossge.necessities.Commands;

import com.crossge.necessities.Janet.JanetWarn;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdWarn extends Cmd {
    JanetWarn warns = new JanetWarn();

    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to warn and a reason.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player target = sender.getServer().getPlayer(uuid);
        String name = "Console";
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (target.hasPermission("Necessities.antiPWarn")) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not warn someone who has Necessities.antiPWarn.");
                return true;
            }
            name = p.getName();
        }
        String reason = "";
        for (int i = 1; i < args.length; i++)
            reason += args[i] + " ";
        reason = reason.trim();
        if (sender instanceof Player && sender.hasPermission("Necessities.colorchat")) {
            if (sender.hasPermission("Necessities.magicchat"))
                reason = ChatColor.translateAlternateColorCodes('&', reason);
            else
                reason = ChatColor.translateAlternateColorCodes('&', reason.replaceAll("&k", ""));
        } else if (!(sender instanceof Player))
            reason = ChatColor.translateAlternateColorCodes('&', reason);
        warns.warn(target.getUniqueId(), reason, name);
        return true;
    }
}