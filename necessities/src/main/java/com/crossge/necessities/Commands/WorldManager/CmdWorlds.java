package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CmdWorlds implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        String levels = "";
        ArrayList<String> worlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < worlds.size() - 1; i++)
            levels += worlds.get(i) + ", ";
        levels += "and " + worlds.get(worlds.size() - 1) + ".";
        sender.sendMessage(Necessities.getVar().getMessages() + "The worlds are: " + ChatColor.WHITE + levels);
        return true;
    }
}