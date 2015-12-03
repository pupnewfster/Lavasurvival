package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.Lavasurvival;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdRules extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.getInventory().contains(Lavasurvival.INSTANCE.getRules())) {
                p.getInventory().addItem(Lavasurvival.INSTANCE.getRules());
                sender.sendMessage(ChatColor.GOLD + "You have been given the rules.");
            } else
                sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You already have a copy of the rules.");
        } else
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "You do not have an inventory.");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public String getName() {
        return "rules";
    }
}