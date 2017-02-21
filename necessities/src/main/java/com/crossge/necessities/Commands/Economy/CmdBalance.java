package com.crossge.necessities.Commands.Economy;

import com.crossge.necessities.Economy.Economy;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdBalance implements EconomyCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Economy eco = Necessities.getEconomy();
        Variables var = Necessities.getVar();
        if (args.length == 0 && sender instanceof Player)
            sender.sendMessage(var.getMessages() + "Balance: " + var.getMoney() + eco.format(eco.getBalance(((Player) sender).getUniqueId())));
        else if (args.length != 0) {
            String playersName;
            UUID uuid = Utils.getID(args[0]);
            if (uuid == null) {
                uuid = Utils.getOfflineID(args[0]);
                if (uuid == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                    sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player does not exist or has not joined the server. If the player is offline, please use the full and most recent name.");
                    return true;
                }
                playersName = Bukkit.getOfflinePlayer(uuid).getName();
            } else
                playersName = Bukkit.getPlayer(uuid).getName();
            sender.sendMessage(var.getObj() + Utils.ownerShip(playersName) + var.getMessages() + " balance is: " + var.getMoney() + eco.format(eco.getBalance(uuid)));
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must be logged in to see your balance.");
        return true;
    }
}