package com.crossge.necessities.Commands;

import com.crossge.necessities.RankManager.User;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdUnban implements Cmd {
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
        User u = um.getUser(uuid);
        String name = console.getName().replaceAll(":", "");
        if (sender instanceof Player)
            name = sender.getName();
        BanList bans = Bukkit.getBanList(BanList.Type.NAME);
        String theirName = u.getName();
        if (!bans.isBanned(theirName)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player is not banned.");
            return true;
        }
        bans.pardon(theirName);
        Bukkit.broadcastMessage(var.getMessages() + name + " unbanned " + theirName + ".");
        return true;
    }
}