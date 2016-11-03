package com.crossge.necessities.Commands;

import com.crossge.necessities.Janet.JanetLog;
import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRagequit implements Cmd {
    private JanetLog log = Necessities.getInstance().getLog();

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.kickPlayer(ChatColor.DARK_RED + "RAGEQUIT!");
            log.log(player.getName() + " RAGEQUIT the server!");
            Bukkit.broadcastMessage(var.getMessages() + player.getDisplayName() + ChatColor.DARK_RED + " RAGEQUIT" + var.getMessages() + " the server!");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot ragequit you would kill the server.");
        return true;
    }
}