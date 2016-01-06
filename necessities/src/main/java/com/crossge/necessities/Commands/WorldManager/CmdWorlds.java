package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CmdWorlds extends WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        String levels = "";
        ArrayList<String> worlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds())
            worlds.add(world.getName());
        for (int i = 0; i < worlds.size() - 1; i++)
            levels += worlds.get(i) + ", ";
        levels += "and " + worlds.get(worlds.size() - 1) + ".";
        sender.sendMessage(var.getMessages() + "The worlds are: " + ChatColor.WHITE + levels);
        return true;
    }
}