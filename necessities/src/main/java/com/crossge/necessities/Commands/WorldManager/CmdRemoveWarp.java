package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import com.crossge.necessities.WorldManager.WarpManager;
import org.bukkit.command.CommandSender;

public class CmdRemoveWarp implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a name for the warp you wish to remove.");
            return true;
        }
        WarpManager warps = Necessities.getWarps();
        if (!warps.isWarp(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "There is no warp by that name.");
            return true;
        }
        warps.remove(args[0]);
        sender.sendMessage(var.getMessages() + "Removed the warp named " + var.getObj() + args[0] + var.getMessages() + ".");
        return true;
    }
}