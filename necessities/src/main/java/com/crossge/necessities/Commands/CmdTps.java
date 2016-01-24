package com.crossge.necessities.Commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class CmdTps extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        sender.sendMessage(form.getTPS());
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        sender.sendMessage(var.getMessages() + "Max Memory: " + var.getObj() + runtime.maxMemory() / mb + var.getMessages() + " mb.");
        sender.sendMessage(var.getMessages() + "Total Memory: " + var.getObj() + runtime.totalMemory() / mb + var.getMessages() + " mb.");
        sender.sendMessage(var.getMessages() + "Free Memory: " + var.getObj() + runtime.freeMemory() / mb + var.getMessages() + " mb.");
        sender.sendMessage(var.getMessages() + "Used Memory: " + var.getObj() + (runtime.totalMemory() - runtime.freeMemory()) / mb + var.getMessages() + " mb.");
        for (World w : Bukkit.getWorlds()) {
            sender.sendMessage(var.getMessages() + "World: " + var.getObj() + w.getName());
            sender.sendMessage(var.getMessages() + "    Entities Loaded: " + var.getObj() + w.getEntities().size());
        }
        return true;
    }
}