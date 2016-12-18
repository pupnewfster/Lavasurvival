package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdSetrank implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you enter a user and a rank to set the user's rank to.");
            return true;
        }
        GetUUID get = Necessities.getUUID();
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            uuid = get.getOfflineID(args[0]);
            if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.");
                return true;
            }
        }
        UserManager um = Necessities.getUM();
        RankManager rm = Necessities.getRM();
        User u = um.getUser(uuid);
        Rank r = rm.getRank(Utils.capFirst(args[1]));
        if (r == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That rank does not exist");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("Necessities.rankmanager.setranksame") && (rm.getOrder().indexOf(um.getUser(player.getUniqueId()).getRank()) - rm.getOrder().indexOf(u.getRank()) <= 0 ||
                    rm.getOrder().indexOf(um.getUser(player.getUniqueId()).getRank()) - rm.getOrder().indexOf(r) <= 0)) {
                player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not change the rank of someone higher than you.");
                return true;
            }
            name = player.getName();
        }
        um.updateUserRank(u, uuid, r);
        Bukkit.broadcastMessage(var.getMessages() + name + " set " + Utils.ownerShip(get.nameFromString(uuid.toString())) + " rank to " + u.getRank().getName() + ".");
        return true;
    }
}