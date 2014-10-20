package me.eddiep.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdKick extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You must enter a player to kick and a reason.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Invalid player.");
            return true;
        }
        Player target = sender.getServer().getPlayer(uuid);
        String name = "Console";
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (rm.getOrder().indexOf(um.getUser(p.getUniqueId()).getRank()) - rm.getOrder().indexOf(um.getUser(uuid).getRank()) <= 0) {
                p.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You may not kick someone of the same rank.");
                return true;
            }
            name = p.getName();
        }
        String reason = "";
        for (int i = 1; i < args.length; i++)
            reason += args[i] + " ";
        reason = ChatColor.translateAlternateColorCodes('&', reason.trim());
        if(reason.equals(""))
            Bukkit.broadcastMessage(ChatColor.GOLD + name + " kicked " + ChatColor.RED + target.getName());
        else
            Bukkit.broadcastMessage(ChatColor.GOLD + name + " kicked " + ChatColor.RED + target.getName() + ChatColor.GOLD + " for " + ChatColor.RED + reason);
        target.kickPlayer(reason.equals("") ? "You were kicked." : reason);
        return true;
    }
}