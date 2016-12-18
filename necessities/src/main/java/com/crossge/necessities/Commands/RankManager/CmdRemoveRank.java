package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;

public class CmdRemoveRank implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you to enter a rank to remove.");
            return true;
        }
        RankManager rm = Necessities.getRM();
        Rank rank = rm.getRank(Utils.capFirst(args[0]));
        if (rank == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That rank does not exists.");
            return true;
        }
        sender.sendMessage(var.getObj() + rank.getName() + var.getMessages() + " deleted and removed from list of ranks.");
        rm.removeRank(rank);
        return true;
    }
}