package me.eddiep.commands;

import me.eddiep.Lavasurvival;
import me.eddiep.ggbot.GGBotWarn;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdWarn extends Cmd {
    GGBotWarn warns = Lavasurvival.INSTANCE.getGGBotWarn();

    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You must enter a player to warn and a reason.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Invalid player.");
            return true;
        }
        String name = "Console";
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (rm.hasRank(um.getUser(p.getUniqueId()).getRank(), um.getUser(uuid).getRank())) {
                p.sendMessage(ChatColor.DARK_RED + "Error: You may not warn someone with a higher or equal rank.");
                return true;
            }
            name = p.getName();
        }
        String reason = "";
        for (int i = 1; i < args.length; i++)
            reason += args[i] + " ";
        warns.warn(uuid, ChatColor.translateAlternateColorCodes('&', reason.trim()), name);
        return true;
    }
}