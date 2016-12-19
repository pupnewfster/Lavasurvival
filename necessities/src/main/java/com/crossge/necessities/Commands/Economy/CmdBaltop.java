package com.crossge.necessities.Commands.Economy;

import com.crossge.necessities.Economy.Economy;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdBaltop implements EconomyCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        int page = 0;
        if (args.length > 0) {
            if (!Utils.legalInt(args[0])) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid baltop page.");
                return true;
            }
            page = Integer.parseInt(args[0]);
        }
        if (args.length == 0 || page == 0)
            page = 1;
        int time = 0;
        Economy eco = Necessities.getEconomy();
        int totalPages = eco.baltopPages();
        if (page > totalPages) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Input a number from 1 to " + Integer.toString(totalPages));
            return true;
        }
        sender.sendMessage(ChatColor.GOLD + "Balance Top Page [" + Integer.toString(page) + "/" + Integer.toString(totalPages) + "]");
        page = page - 1;
        String bal = eco.balTop(page, time);
        while (bal != null) {
            bal = ChatColor.GOLD + Integer.toString((page * 10) + time + 1) + ". " + var.getCatalog() + Necessities.getUUID().nameFromString(bal.split(" ")[0]) + " has: " + var.getMoney() +
                    eco.format(Double.parseDouble(bal.split(" ")[1]));
            sender.sendMessage(bal);
            time++;
            bal = eco.balTop(page, time);
        }
        return true;
    }
}