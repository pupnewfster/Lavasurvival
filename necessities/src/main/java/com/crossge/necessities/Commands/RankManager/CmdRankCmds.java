package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdRankCmds implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a rank to view the commands of.");
            return true;
        }
        Rank r = rm.getRank(Utils.capFirst(args[0]));
        if (r == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid rank.");
            return true;
        }
        sender.sendMessage(var.getMessages() + "Commands for " + var.getObj() + r.getName() + var.getMessages() + ": " + ChatColor.WHITE + r.getCommands());
        return true;
    }
}