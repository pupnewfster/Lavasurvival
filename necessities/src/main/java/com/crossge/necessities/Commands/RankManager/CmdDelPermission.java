package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.RankManager.Rank;
import org.bukkit.command.CommandSender;

public class CmdDelPermission implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires a rank and a permission node to remove from that rank.");
            return true;
        }
        Rank r = rm.getRank(args[0]);
        if (r == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That is not a valid rank.");
            return true;
        }
        String node = args[1];
        rm.updateRankPerms(r, node, true);
        sender.sendMessage(var.getMessages() + "Removed " + var.getObj() + node + var.getMessages() + " from " + var.getObj() + form.ownerShip(r.getName()) + var.getMessages() + " permissions.");
        return true;
    }
}