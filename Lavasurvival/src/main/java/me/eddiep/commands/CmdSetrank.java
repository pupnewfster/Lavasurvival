package me.eddiep.commands;

import java.util.UUID;
import me.eddiep.ranks.Rank;
import me.eddiep.ranks.UserInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSetrank extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: Format requires you enter a user and a rank to set the user's rank to.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if(uuid == null) {
            uuid = get.getOfflineID(args[0]);
            if(uuid == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: Invalid player.");
                return true;
            }
        }
        UserInfo u = um.getUser(uuid);
        String rName = args[1].toUpperCase();
        if (args[1].length() > 1)//should normally be true but if not we do not want any errors
            rName = args[1].substring(0, 1).toUpperCase() + args[1].substring(1).toLowerCase();
        Rank r = rm.getRank(rName);
        if(r == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: That rank does not exist");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(rm.hasRank(um.getUser(player.getUniqueId()).getRank(), r)) {
                player.sendMessage(ChatColor.DARK_RED + "Error: You may not change the rank of someone higher than you.");
                return true;
            }
            name = player.getName();
        }
        um.updateUserRank(u, r);
        Bukkit.broadcastMessage(name + " set " + ownership(get.nameFromString(uuid.toString())) + " rank to " + u.getRank().getName() + ".");
        return true;
    }

    private String ownership(String name) {
        if(name.endsWith("s"))
            return name + "'";
        return name + "'s";
    }
}