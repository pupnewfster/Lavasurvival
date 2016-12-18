package com.crossge.necessities.Commands;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class CmdBanIP implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter an player to ban.");
            return true;
        }
        GetUUID get = Necessities.getUUID();
        String name = Necessities.getConsole().getName().replaceAll(":", "");
        UUID uuid = get.getID(args[0]);
        if (uuid != null) {
            Player target = Bukkit.getPlayer(uuid);
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (target.hasPermission("Necessities.antiBan")) {
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
            BanList bans = Bukkit.getBanList(BanList.Type.IP);
            String theirName = target.getName();
            String theirIP = target.getAddress().toString().split("/")[1].split(":")[0];
            target.kickPlayer(reason);
            bans.addBan(theirIP, reason, null, name);
            Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirName + var.getMessages() + (reason.equals("") ? "." : " for " + var.getObj() + reason + var.getMessages() + "."));
            return true;
        }
        boolean validIp = false;
        try {
            Pattern ipAdd = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
            validIp = ipAdd.matcher(args[0]).matches();
        } catch (Exception ignored) {
        }
        if (!validIp) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid ip.");
            return true;
        }
        if (sender instanceof Player)
            name = sender.getName();
        String reason = "";
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++)
                reason += args[i] + " ";
            reason = ChatColor.translateAlternateColorCodes('&', reason.trim());
        }
        BanList bans = Bukkit.getBanList(BanList.Type.IP);
        String theirIP = args[0];
        for (Player t : Bukkit.getOnlinePlayers())
            if (t.getAddress().toString().split("/")[1].split(":")[0].equals(theirIP)) {
                t.kickPlayer(reason);
                break;
            }
        bans.addBan(theirIP, reason, null, name);
        Bukkit.broadcastMessage(var.getMessages() + name + " banned " + var.getObj() + theirIP + var.getMessages() + (reason.equals("") ? "." : " for " + var.getObj() + reason + var.getMessages() + "."));
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