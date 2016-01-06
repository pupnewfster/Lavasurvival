package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.RankManager.Rank;
import org.bukkit.command.CommandSender;

public class CmdAddSubrank extends RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a rank and a subrank to add to that rank.");
            return true;
        }
        Rank r = rm.getRank(form.capFirst(args[0]));
        if (r == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That is not a valid rank.");
            return true;
        }
        String subrank = args[1];
        if (rm.validSubrank(subrank)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That subrank does not exist");
            return true;
        }
        subrank = rm.getSub(subrank);
        rm.updateRankSubrank(r, subrank, false);
        sender.sendMessage(var.getMessages() + "Added " + var.getObj() + subrank + var.getMessages() + " to " + var.getObj() + form.ownerShip(r.getName()) + var.getMessages() + " subranks.");
        return true;
    }
}