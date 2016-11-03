package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Utils;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CmdDelSubrankUser implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a user and a subrank to remove from that user.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null)
            uuid = get.getOfflineID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player has not joined the server. If the player is offline, please use the full and most recent name.");
            return true;
        }
        String subrank = args[1];
        if (rm.validSubrank(subrank)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That subrank does not exist");
            return true;
        }
        subrank = rm.getSub(subrank);
        um.updateUserSubrank(uuid, subrank, true);
        sender.sendMessage(var.getMessages() + "Removed " + var.getObj() + subrank + var.getMessages() + " from " + var.getObj() + Utils.ownerShip(get.nameFromString(uuid.toString())) + var.getMessages() + " subranks.");
        return true;
    }
}