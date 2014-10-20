package me.eddiep.commands;

import me.eddiep.Lavasurvival;
import me.eddiep.game.Gamemode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdEndGame extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if(Gamemode.getCurrentGame() != null) {
            Gamemode.getCurrentGame().endRound();
            Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + " ended the game early.");
        } else
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "There is no game in progress.");
        return true;
    }
}