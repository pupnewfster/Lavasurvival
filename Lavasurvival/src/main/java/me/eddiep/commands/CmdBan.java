package me.eddiep.commands;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdBan extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You must enter a player to ban and a reason.");
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
            if (rm.hasRank(um.getUser(p.getUniqueId()).getRank(), rm.getRank("Op"))) {
                p.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You may not ban ops or higher.");
                return true;
            }
            name = p.getName();
        }
        String reason = null;
        if (args.length > 1) {
            reason = "";
            for (int i = 1; i < args.length; i++)
                reason += args[i] + " ";
            reason = ChatColor.translateAlternateColorCodes('&', reason.trim());
        }
        if(reason == null)
            Bukkit.broadcastMessage(ChatColor.GOLD + name + " banned " + ChatColor.RED + target.getName());
        else
            Bukkit.broadcastMessage(ChatColor.GOLD + name + " banned " + ChatColor.RED + target.getName() + ChatColor.GOLD + " for " + ChatColor.RED + reason);
        String theirName = target.getName();
        target.kickPlayer(reason == null ? "You were banned." : reason);
        BanList bans = Bukkit.getBanList(BanList.Type.NAME);
        bans.addBan(theirName, reason, null, name);
        return true;
    }
}