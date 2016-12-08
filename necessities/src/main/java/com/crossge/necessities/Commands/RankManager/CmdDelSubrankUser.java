package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CmdDelSubrankUser implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a user and a subrank to remove from that user.");
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
        RankManager rm = Necessities.getInstance().getRM();
        String subrank = args[1];
        if (rm.validSubrank(subrank)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That subrank does not exist");
            return true;
        }
        subrank = rm.getSub(subrank);
        Necessities.getInstance().getUM().updateUserSubrank(uuid, subrank, true);
        sender.sendMessage(var.getMessages() + "Removed " + var.getObj() + subrank + var.getMessages() + " from " + var.getObj() + Utils.ownerShip(get.nameFromString(uuid.toString())) + var.getMessages() + " subranks.");
        return true;
    }
}