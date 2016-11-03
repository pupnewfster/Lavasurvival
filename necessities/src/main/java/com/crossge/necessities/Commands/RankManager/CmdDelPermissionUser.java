package com.crossge.necessities.Commands.RankManager;

import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CmdDelPermissionUser implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a user and a permission node to remove from that user.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            uuid = get.getOfflineID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player has not joined the server. If the player is offline, please use the full and most recent name.");
                return true;
            }
        }
        String node = args[1];
        um.updateUserPerms(uuid, node, true);
        sender.sendMessage(var.getMessages() + "Removed " + var.getObj() + node + var.getMessages() + " from " + var.getObj() + form.ownerShip(get.nameFromString(uuid.toString())) + var.getMessages() + " permissions.");
        return true;
    }
}