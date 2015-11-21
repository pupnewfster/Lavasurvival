package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.ranks.UserInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
            target = Bukkit.getPlayer(uuid);
        UserInfo u = um.getUser(uuid);
        if (u.getRank().getPrevious() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + target.getName() + " is already the lowest rank.");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (rm.getOrder().indexOf(um.getUser(player.getUniqueId()).getRank()) - rm.getOrder().indexOf(um.getUser(uuid).getRank()) <= 0) {
                player.sendMessage(ChatColor.DARK_RED + "Error: You may not demote people a higher or equal rank.");
                return true;
            }
            name = player.getName();
        }
        um.updateUserRank(u, u.getRank().getPrevious());
        Bukkit.broadcastMessage(name + " demoted " + get.nameFromString(uuid.toString()) + " to " + u.getRank().getName() + ".");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1)
            return new ArrayList<String>();
        List<String> complete = new ArrayList<String>();
        String search = args[args.length - 1];
        if (sender instanceof Player) {
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().startsWith(search) && um.getUser(p.getUniqueId()).getRank().getPrevious() != null && ((Player) sender).canSee(p) &&
                    rm.getOrder().indexOf(um.getUser(((Player) sender).getUniqueId()).getRank()) - rm.getOrder().indexOf(um.getUser(p.getUniqueId()).getRank()) > 0)
                    complete.add(p.getName());
        } else
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().startsWith(search) && um.getUser(p.getUniqueId()).getRank().getPrevious() != null)
                    complete.add(p.getName());
        return complete;
    }

    @Override
    public String getName() {
        return "demote";
    }
}