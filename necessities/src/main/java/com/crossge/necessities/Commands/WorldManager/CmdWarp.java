package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.WorldManager.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdWarp extends WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a name for the warp you wish to teleport to.");
            return true;
        }
        if (!warps.isWarp(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That warp does not exists.");
            return true;
        }
        if (sender instanceof Player) {
            User u = um.getUser(((Player) sender).getUniqueId());
            Warp w = warps.getWarp(args[0]);
            if (!w.hasDestination())
                return true;
            ((Player) sender).teleport(w.getDestination());
            sender.sendMessage(var.getMessages() + "Teleporting to " + var.getObj() + args[0] + var.getMessages() + ".");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must be a player to teleport warps.");
        return true;
    }
}