package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdPromote extends RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a user to promote.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        Player target;
        if (uuid == null) {
            uuid = get.getOfflineID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player has not joined the server. If the player is offline, please use the full and most recent name.");
                return true;
            }
            target = Bukkit.getOfflinePlayer(uuid).getPlayer();
        } else
            target = Bukkit.getPlayer(uuid);
        User u = um.getUser(uuid);
        if (u.getRank().getNext() == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + target.getName() + " is already the highest rank.");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("Necessities.rankmanager.setranksame") && rm.getOrder().indexOf(um.getUser(player.getUniqueId()).getRank()) - rm.getOrder().indexOf(u.getRank()) <= 1) {
                player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not promote people to same rank as yourself.");
                return true;
            }
            name = player.getName();
        }
        um.updateUserRank(u, uuid, u.getRank().getNext());
        Bukkit.broadcastMessage(var.getPromote() + name + " promoted " + get.nameFromString(uuid.toString()) + " to " + u.getRank().getName() + ".");
        return true;
    }
}