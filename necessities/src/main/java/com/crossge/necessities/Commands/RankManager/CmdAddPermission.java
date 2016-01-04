package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.RankManager.Rank;
import org.bukkit.command.CommandSender;

public class CmdAddPermission extends RankCmd {

    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a rank and a permission node to add to that rank.");
            return true;
        }
        Rank r = rm.getRank(form.capFirst(args[0]));
        if (r == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That is not a valid rank.");
            return true;
        }
        String node = args[1];
        rm.updateRankPerms(r, node, false);
        sender.sendMessage(var.getMessages() + "Added " + var.getObj() + node + var.getMessages() + " to " + var.getObj() + plural(r.getName()) + var.getMessages() +
                " permissions.");
        return true;
    }

    private String plural(String name) {
        if (name.endsWith("s"))
            return name + "'";
        return name + "'s";
    }
}