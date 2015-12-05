package com.crossge.necessities.Commands.RankManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdReloadPermissions extends RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Reloading all permissions.");
        rm.reloadPermissions();
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All permissions reloaded.");
        return true;
    }
}