package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CmdUnloadWorld implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a worldname to unload.");
            return true;
        }
        if (wm.worldUnloaded(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world is not loaded.");
            return true;
        }
        if (!wm.worldExists(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world does not exist.");
            return true;
        }
        String name = Bukkit.getWorld(args[0]).getName();
        wm.unloadWorld(args[0]);
        sender.sendMessage(var.getMessages() + "Unloaded " + var.getObj() + name + var.getMessages() + ".");
        return true;
    }
}