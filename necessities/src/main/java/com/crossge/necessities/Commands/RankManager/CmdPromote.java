package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdPromote implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length != 1) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a user to promote.");
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
        UserManager um = Necessities.getInstance().getUM();
        User u = um.getUser(uuid);
        String targetName = get.nameFromString(uuid.toString());
        if (u.getRank().getNext() == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + targetName + " is already the highest rank.");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            RankManager rm = Necessities.getInstance().getRM();
            if (!player.hasPermission("Necessities.rankmanager.setranksame") && rm.getOrder().indexOf(um.getUser(player.getUniqueId()).getRank()) - rm.getOrder().indexOf(u.getRank()) <= 1) {
                player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not promote people to same rank as yourself.");
                return true;
            }
            name = player.getName();
        }
        um.updateUserRank(u, uuid, u.getRank().getNext());
        Bukkit.broadcastMessage(var.getPromote() + name + " promoted " + targetName + " to " + u.getRank().getName() + ".");
        return true;
    }
}