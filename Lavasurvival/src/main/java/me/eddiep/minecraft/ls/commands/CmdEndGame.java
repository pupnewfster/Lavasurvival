package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.game.Gamemode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdEndGame extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if(Gamemode.getCurrentGame() != null) {
            Gamemode.getCurrentGame().endRound();
            Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + " ended the game early.");
        } else
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "There is no game in progress.");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public String getName() {
        return "endgame";
    }
}