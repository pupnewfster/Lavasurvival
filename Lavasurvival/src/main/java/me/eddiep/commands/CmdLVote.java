package me.eddiep.commands;

import me.eddiep.game.Gamemode;
import me.eddiep.system.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdLVote extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Gamemode.getCurrentGame() == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: there is no game running.");
                return true;
            }
            PlayerListener listener = Gamemode.getPlayerListener();
            if (listener.voted.contains(player)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You already voted!");
                return true;
            }
            try {
                int number = Integer.parseInt(args[0]);
                number--;
                if (number >= Gamemode.nextMaps.length) {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.nextMaps.length + ").");
                    return true;
                }
                listener.voted.add(player);
                Gamemode.votes[number]++;
                player.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + "" + ChatColor.BOLD + "You voted for " + Gamemode.nextMaps[number].getName() + "!");
            } catch (Throwable t) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.nextMaps.length + ").");
            }
        } else
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used in game..");
        return true;
    }
}
