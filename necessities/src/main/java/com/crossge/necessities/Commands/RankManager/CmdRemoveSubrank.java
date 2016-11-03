package com.crossge.necessities.Commands.RankManager;

import org.bukkit.command.CommandSender;

public class CmdRemoveSubrank implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you to enter a subrank to delete.");
            return true;
        }
        String subrank = args[0];
        if (rm.validSubrank(subrank)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That subrank does not exist.");
            return true;
        }
        rm.removeSubrank(rm.getSub(subrank));
        sender.sendMessage(var.getObj() + subrank + var.getMessages() + " deleted and removed from list of subranks.");
        return true;
    }
}