package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.RankManager.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdRanks extends RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (rm.getOrder().isEmpty())
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "No ranks are set.");
        else {
            String ranks = var.getMessages() + "Available ranks are: " + ChatColor.WHITE;
            for (Rank r : rm.getOrder())
                ranks += r.getName() + var.getMessages() + ", " + ChatColor.WHITE;
            ranks = ranks.substring(0, ranks.length() - 2).trim();
            sender.sendMessage(ranks);
        }
        return true;
    }
}