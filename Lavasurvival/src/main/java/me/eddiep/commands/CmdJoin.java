package me.eddiep.commands;

import me.eddiep.game.Gamemode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdJoin extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (Gamemode.getCurrentGame() == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: there is no game running.");
                return true;
            }
            if (Gamemode.getCurrentGame().isSpectator(p))
                Gamemode.getCurrentGame().playerJoin(p);
            else
                p.sendMessage(ChatColor.DARK_RED + "You are already playing the current game!");
        } else
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used in game..");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }
}