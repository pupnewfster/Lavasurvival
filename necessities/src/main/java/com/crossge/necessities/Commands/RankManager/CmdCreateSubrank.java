package com.crossge.necessities.Commands.RankManager;

import org.bukkit.command.CommandSender;

public class CmdCreateSubrank implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you to enter a name for the subrank you are creating.");
            return true;
        }
        String subrank = args[0];
        if (!rm.validSubrank(subrank)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That subrank already exists.");
            return true;
        }
        rm.addSubrank(subrank);
        sender.sendMessage(var.getObj() + subrank + var.getMessages() + " created and added to list of subranks.");
        return true;
    }
}