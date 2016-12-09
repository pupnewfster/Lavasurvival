package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import com.crossge.necessities.WorldManager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CmdLoadWorld implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a world name to load.");
            return true;
        }
        WorldManager wm = Necessities.getWM();
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