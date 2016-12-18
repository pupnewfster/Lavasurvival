package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import com.crossge.necessities.WorldManager.WarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCreateWarp implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a name for the warp you wish to create.");
            return true;
        }
        WarpManager warps = Necessities.getWarps();
        if (warps.isWarp(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That warp already exists.");
            return true;
        }
        if (sender instanceof Player) {
            warps.create(args[0], ((Player) sender).getLocation());
            sender.sendMessage(var.getMessages() + "Created warp named " + var.getObj() + args[0] + var.getMessages() + " at your current location.");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must be a player to create warps.");
        return true;
    }
}