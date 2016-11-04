package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Variables;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdSubranks implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        RankManager rm = Necessities.getInstance().getRM();
        if (rm.getSubranks().isEmpty())
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "No subranks are set.");
        else {
            String subranks = var.getMessages() + "Available subranks are: " + ChatColor.WHITE;
            for (String subrank : rm.getSubranks())
                subranks += subrank + var.getMessages() + ", " + ChatColor.WHITE;
            subranks = subranks.substring(0, subranks.length() - 2).trim();
            sender.sendMessage(subranks);
        }
        return true;
    }
}