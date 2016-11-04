package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import com.crossge.necessities.WorldManager.WorldManager;
import org.bukkit.command.CommandSender;

public class CmdRemoveWorld implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a worldname to remove from the files.");
            return true;
        }
        WorldManager wm = Necessities.getInstance().getWM();
        if (wm.worldExists(args[0])) {//TODO: perhaps make it so you you can just remove without unloading if you want
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world is loaded before you remove a world from the configs unload it.");
            return true;
        }
        if (!wm.worldUnloaded(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world does not exist.");
            return true;
        }
        wm.removeWorld(args[0]);//TODO: perhaps add the equivalent of mvconfirm
        sender.sendMessage(var.getMessages() + "Removed " + var.getObj() + args[0] + var.getMessages() + " from the files.");
        return true;
    }
}