package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;

public class CmdAddPermSubrank implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length < 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a subrank and a permission node to add to that subrank.");
            return true;
        }
        RankManager rm = Necessities.getRM();
        if (rm.validSubrank(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That is not a valid subrank.");
            return true;
        }
        String subrank = rm.getSub(args[0]);
        String node = args[1];
        rm.updateSubPerms(subrank, node, false);
        sender.sendMessage(var.getMessages() + "Added " + var.getObj() + node + var.getMessages() + " to " + var.getObj() + Utils.ownerShip(Utils.capFirst(subrank)) + var.getMessages() + " permissions.");
        return true;
    }
}