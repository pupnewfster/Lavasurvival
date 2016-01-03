package com.crossge.necessities.Commands.RankManager;

import org.bukkit.command.CommandSender;

public class CmdAddPermSubrank extends RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a subrank and a permission node to add to that subrank.");
            return true;
        }
        if (rm.validSubrank(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That is not a valid subrank.");
            return true;
        }
        String subrank = rm.getSub(args[0]);
        String node = args[1];
        rm.updateSubPerms(subrank, node, false);
        sender.sendMessage(var.getMessages() + "Added " + var.getObj() + node + var.getMessages() + " to " + var.getObj() + plural(form.capFirst(subrank)) +
                var.getMessages() + " permissions.");
        return true;
    }

    private String plural(String name) {
        if (name.endsWith("s"))
            return name + "'";
        return name + "'s";
    }
}