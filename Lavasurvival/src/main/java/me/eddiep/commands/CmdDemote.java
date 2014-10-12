package me.eddiep.commands;

import me.eddiep.ranks.UserInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdDemote extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: You must enter a user to demote.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        Player target;
        if (uuid == null) {
            uuid = get.getOfflineID(args[0]);
            if (uuid == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: Invalid player.");
                return true;
            }
            target = Bukkit.getOfflinePlayer(uuid).getPlayer();
        } else
            target = sender.getServer().getPlayer(uuid);
        UserInfo u = um.getUser(uuid);
        if (u.getRank().getPrevious() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + target.getName() + " is already the lowest rank.");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (rm.hasRank(um.getUser(player.getUniqueId()).getRank(), u.getRank())) {
                player.sendMessage(ChatColor.DARK_RED + "Error: You may not demote people a higher or equal rank.");
                return true;
            }
            name = player.getName();
        }
        um.updateUserRank(u, u.getRank().getPrevious());
        Bukkit.broadcastMessage(name + " demoted " + get.nameFromString(uuid.toString()) + " to " + u.getRank().getName() + ".");
        return true;
    }
}