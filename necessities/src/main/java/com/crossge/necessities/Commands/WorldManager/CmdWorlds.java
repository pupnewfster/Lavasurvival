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
        ArrayList<String> worlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toCollection(ArrayList::new));
        StringBuilder levelsBuilder = new StringBuilder();
        for (int i = 0; i < worlds.size() - 1; i++)
            levelsBuilder.append(worlds.get(i)).append(", ");
        levelsBuilder.append("and ").append(worlds.get(worlds.size() - 1)).append(".");
        sender.sendMessage(Necessities.getVar().getMessages() + "The worlds are: " + ChatColor.WHITE + levelsBuilder.toString());
        return true;
    }
}