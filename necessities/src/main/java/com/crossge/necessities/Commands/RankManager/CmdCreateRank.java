package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;

public class CmdCreateRank implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length < 3) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you to enter a rank to create the rank that comes before and the rank that comes after.");
            return true;
        }
        RankManager rm = Necessities.getRM();
        if (rm.getRank(Utils.capFirst(args[0])) != null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That rank already exists.");
            return true;
        }
        String rank = Utils.capFirst(args[0]);
        Rank previous = rm.getRank(Utils.capFirst(args[1]));
        Rank next = rm.getRank(Utils.capFirst(args[2]));
        if (!args[1].equalsIgnoreCase("null") && previous == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The rank you inputted as previous does not exists.");
            return true;
        }
        if (!args[2].equalsIgnoreCase("null") && next == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The rank you inputted as next does not exist.");
            return true;
        }
        if (args[1].equalsIgnoreCase("null") && args[2].equalsIgnoreCase("null") && !rm.getOrder().isEmpty()) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Enter a valid previous or next rank.");
            return true;//both are null but there are ranks so they have to have either a next or previous
        } else if (args[1].equalsIgnoreCase("null") && args[2].equalsIgnoreCase("null") && rm.getOrder().isEmpty())
            rm.addRank(rank, previous, next);//both are null
        else if (args[1].equalsIgnoreCase("null") && next != null && next.getPrevious() == null)
            rm.addRank(rank, previous, next);//add to front of list
        else if (args[2].equalsIgnoreCase("null") && previous != null && previous.getNext() == null)
            rm.addRank(rank, previous, next);//add to end of list
        else if (previous != null && next != null && previous.getNext().equals(next))
            rm.addRank(rank, previous, next);//are consecutive
        else {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must put the new rank in between two consecutive ranks.");
            return true;
        }
        sender.sendMessage(var.getObj() + rank + var.getMessages() + " created and added to list of ranks.");
        return true;
    }
}