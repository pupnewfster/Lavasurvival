package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.game.Gamemode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdEndGame implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (Gamemode.getCurrentGame() != null) {
            if (args.length > 0) {
                if (Gamemode.getCurrentMap().getName().equalsIgnoreCase(args[0]))
                    sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "That map is already the current map.");
                else if (!Gamemode.getCurrentGame().setNextMap(args[0], args.length > 1 ? args[1] : null))
                    sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "That map is invalid or that gamemode does not exist for that map.");
                else {
                    Gamemode.getCurrentGame().endRound(true, false);
                    Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + " ended the game early. New game will be on " + args[0] + (args.length > 1 ? " and will be " + args[1] : "") + ".");
                    return true;
                }
            }
            Gamemode.getCurrentGame().endRound(false, false);
            Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + " ended the game early.");
        } else
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "There is no game in progress.");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "endgame";
    }
}