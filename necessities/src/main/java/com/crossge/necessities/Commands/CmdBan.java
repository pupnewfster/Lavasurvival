package com.crossge.necessities.Commands;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CmdBan extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to ban.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null)
            uuid = get.getOfflineID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = console.getName().replaceAll(":", "");
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (target.getPlayer() != null && target.getPlayer().hasPermission("Necessities.antiBan")) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not ban someone who has Necessities.antiBan.");
                return true;
            }
            name = p.getName();
        }
        String reason = "";
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++)
                reason += args[i] + " ";
            reason = ChatColor.translateAlternateColorCodes('&', reason.trim());
        }
        BanList bans = Bukkit.getBanList(BanList.Type.NAME);
        String theirName = target.getName();
        if (target.getPlayer() != null)
            target.getPlayer().kickPlayer(reason);
        bans.addBan(theirName, reason, null, name);
        Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirName + var.getMessages() + (reason.equals("") ? "." : " for " + var.getObj() + reason + var.getMessages() + "."));
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        String search = "";
        if (args.length > 0)
            search = args[args.length - 1];
        if (sender instanceof Player) {
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().startsWith(search) && !p.hasPermission("Necessities.antiBan") && ((Player) sender).canSee(p))
                    complete.add(p.getName());
        } else
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().startsWith(search))
                    complete.add(p.getName());
        return complete;
    }
}