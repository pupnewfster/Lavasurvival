package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdWarps extends WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        sender.sendMessage((warps.getWarps().equals("") ? var.getEr() + "Error: " + var.getErMsg() + "There are no warps set." : var.getMessages() + "Available warps: " + ChatColor.WHITE + warps.getWarps()));
        return true;
    }
}