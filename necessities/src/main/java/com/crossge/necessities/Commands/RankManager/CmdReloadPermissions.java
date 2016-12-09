package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdReloadPermissions implements RankCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Reloading all permissions.");
        Necessities.getRM().reloadPermissions();
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All permissions reloaded.");
        return true;
    }
}