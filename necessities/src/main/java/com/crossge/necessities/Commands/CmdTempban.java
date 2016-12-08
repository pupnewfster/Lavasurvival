package com.crossge.necessities.Commands;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class CmdTempban implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length <= 1) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to ban and a duration in minutes.");
            return true;
        }
        GetUUID get = Necessities.getInstance().getUUID();
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            uuid = get.getOfflineID(args[0]);
            if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.");
                return true;
            }
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = Necessities.getInstance().getConsole().getName().replaceAll(":", "");
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (target.getPlayer() != null && target.getPlayer().hasPermission("Necessities.antiBan")) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not ban someone who has Necessities.antiBan.");
                return true;
            }
            name = p.getName();
        }
        int minutes;
        try {
            minutes = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid time, please enter a time in minutes.");
            return true;
        }
        if (minutes < 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid time, please enter a time in minutes.");
            return true;
        }
        String reason = "";
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++)
                reason += args[i] + " ";
            reason = ChatColor.translateAlternateColorCodes('&', reason.trim());
        }
        BanList bans = Bukkit.getBanList(BanList.Type.NAME);
        String theirName = target.getName();
        if (target.getPlayer() != null)
            target.getPlayer().kickPlayer(reason);
        Date date = new Date(System.currentTimeMillis() + minutes * 60 * 1000);
        bans.addBan(theirName, reason, date, name);
        Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirName + var.getMessages() + " for " + var.getObj() + minutes + var.getMessages() + " " + plural(minutes) +
                (reason.equals("") ? "." : " for the reason " + var.getObj() + reason + var.getMessages() + "."));
        return true;
    }

    private String plural(int amount) {
        return amount == 1 ? "minute" : "minutes";
    }
}