package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdDemote implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length != 1) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a user to demote.");
            return true;
        }
        UUID uuid = Utils.getID(args[0]);
        if (uuid == null) {
            uuid = Utils.getOfflineID(args[0]);
            if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.");
                return true;
            }
        }
        UserManager um = Necessities.getUM();
        User u = um.getUser(uuid);
        String targetName = Utils.nameFromString(uuid.toString());
        if (u.getRank().getPrevious() == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + targetName + " is already the lowest rank.");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("Necessities.rankmanager.setranksame") && Necessities.getRM().hasRank(um.getUser(player.getUniqueId()).getRank(), u.getRank())) {
                player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not demote people a higher or equal rank.");
                return true;
            }
            name = player.getName();
        }
        um.updateUserRank(u, uuid, u.getRank().getPrevious());
        Bukkit.broadcastMessage(var.getDemote() + name + " demoted " + targetName + " to " + u.getRank().getName() + ".");
        return true;
    }
}