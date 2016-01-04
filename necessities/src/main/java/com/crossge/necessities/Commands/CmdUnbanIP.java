package com.crossge.necessities.Commands;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class CmdUnbanIP extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter an ip to unban.");
            return true;
        }
        boolean validIp = false;
        try {
            final Pattern ipAdd = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
            validIp = ipAdd.matcher(args[0]).matches();
        } catch (Exception e) {}
        if (!validIp) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid ip.");
            return true;
        }
        String name = console.getName().replaceAll(":", "");
        if (sender instanceof Player)
            name = sender.getName();
        BanList bans = Bukkit.getBanList(BanList.Type.IP);
        String theirIP = args[0];
        if (!bans.isBanned(theirIP)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That ip is not banned.");
            return true;
        }
        bans.pardon(theirIP);
        Bukkit.broadcastMessage(var.getMessages() + name + " unbanned " + theirIP + ".");
        return true;
    }
}