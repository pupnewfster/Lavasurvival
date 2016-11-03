package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.Utils;
import org.bukkit.command.CommandSender;

public class CmdDelSubrank implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a rank and a subrank to remove from that rank.");
            return true;
        }
        Rank r = rm.getRank(args[0]);
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
        rm.updateRankSubrank(r, subrank, true);
        sender.sendMessage(var.getMessages() + "Removed " + var.getObj() + subrank + var.getMessages() + " from " + var.getObj() + Utils.ownerShip(r.getName()) + var.getMessages() + " subranks.");
        return true;
    }
}