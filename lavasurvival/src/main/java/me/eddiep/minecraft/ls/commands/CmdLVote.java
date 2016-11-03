package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.game.Gamemode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdLVote implements Cmd {
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
                game.voteFor(number, player);
            } catch (Throwable t) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid number! Please choose a number between (1 - " + Gamemode.getCurrentGame().getMapsInVote().size() + ").");
            }
        } else
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used in game..");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        if(sender instanceof Player && args.length == 1) {
            if (Gamemode.getCurrentGame().getMapsInVote().size() == 0)
                return complete;
            if ("1".startsWith(args[0]))
                complete.add("1");
            if (Gamemode.getCurrentGame().getMapsInVote().size() == 1)
                return complete;
            if ("2".startsWith(args[0]))
                complete.add("2");
            if (Gamemode.getCurrentGame().getMapsInVote().size() == 2)
                return complete;
            if ("3".startsWith(args[0]))
                complete.add("3");
        }
        return complete;
    }

    @Override
    public String getName() {
        return "lvote";
    }
}