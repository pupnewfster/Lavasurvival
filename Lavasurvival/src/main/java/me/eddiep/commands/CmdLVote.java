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
            Gamemode game = Gamemode.getCurrentGame();
            if (game == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: there is no game running.");
                return true;
            }
            if (game.hasVoted(player)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You already voted!");
                return true;
            }
            try {
                int number = Integer.parseInt(args[0]);
                number--;
                game.voteFor(number);
            } catch (Throwable t) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.getCurrentGame().getMapsInVote().size() + ").");
            }
        } else
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used in game..");
        return true;
    }
}
