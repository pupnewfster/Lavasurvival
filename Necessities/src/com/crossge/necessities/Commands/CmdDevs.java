package com.crossge.necessities.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdDevs extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        sender.sendMessage(var.getMessages() + "The Devs for Necessities are: " + ChatColor.WHITE + ChatColor.ITALIC + "pupnewfster" + var.getMessages() +
                ", " + ChatColor.WHITE + ChatColor.ITALIC + "Mod_Chris" + var.getMessages() + ", " + ChatColor.WHITE + ChatColor.ITALIC + "hypereddie10" + var.getMessages() + ".");
        return true;
    }
}