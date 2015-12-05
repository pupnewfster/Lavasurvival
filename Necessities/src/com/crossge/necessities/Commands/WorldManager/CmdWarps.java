package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdWarps extends WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (warps.getWarps().equals(""))
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "There are no warps set.");
        else
            sender.sendMessage(var.getMessages() + "Available warps: " + ChatColor.WHITE + warps.getWarps());
        return true;
    }
}