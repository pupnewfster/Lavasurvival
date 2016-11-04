package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRagequit implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.kickPlayer(ChatColor.DARK_RED + "RAGEQUIT!");
            Necessities.getInstance().getLog().log(player.getName() + " RAGEQUIT the server!");
            Bukkit.broadcastMessage(var.getMessages() + player.getDisplayName() + ChatColor.DARK_RED + " RAGEQUIT" + var.getMessages() + " the server!");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot ragequit you would kill the server.");
        return true;
    }
}