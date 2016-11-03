package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CmdLoadWorld implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a worldname to load.");
            return true;
        }
        if (wm.worldExists(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world is all ready loaded.");
            return true;
        }
        if (!wm.worldUnloaded(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world does not exist.");
            return true;
        }
        wm.loadWorld(args[0]);
        sender.sendMessage(var.getMessages() + "Loaded world " + var.getObj() + Bukkit.getWorld(args[0]).getName() + var.getMessages() + ".");
        return true;
    }
}