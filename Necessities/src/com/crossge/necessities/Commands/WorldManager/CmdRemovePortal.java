package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.command.CommandSender;

public class CmdRemovePortal extends WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a portal to remove.");
            return true;
        }
        if (!pm.exists(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That portal does not exist.");
            return true;
        }
        pm.remove(args[0]);
        sender.sendMessage(var.getMessages() + "Removed portal named " + var.getObj() + args[0] + var.getMessages() + ".");
        return true;
    }
}