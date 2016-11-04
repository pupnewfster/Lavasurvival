package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class CmdUnbanIP implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter an ip to unban.");
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
        String name = (sender instanceof Player ? sender.getName() : Necessities.getInstance().getConsole().getName().replaceAll(":", ""));
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