package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CmdAddSubrankUser implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a user and a subrank to add for that user.");
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
        String subrank = args[1];
        RankManager rm = Necessities.getRM();
        if (rm.validSubrank(subrank)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That subrank does not exist");
            return true;
        }
        subrank = rm.getSub(subrank);
        Necessities.getUM().updateUserSubrank(uuid, subrank, false);
        sender.sendMessage(var.getMessages() + "Added " + var.getObj() + subrank + var.getMessages() + " to " + var.getObj() + Utils.ownerShip(Utils.nameFromString(uuid.toString())) + var.getMessages() + " subranks.");
        return true;
    }
}