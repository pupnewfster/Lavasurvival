package com.crossge.necessities.Commands;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.Variables;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdUnban implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to ban.");
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
        User u = Necessities.getInstance().getUM().getUser(uuid);
        String name = Necessities.getInstance().getConsole().getName().replaceAll(":", "");
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