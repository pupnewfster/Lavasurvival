package me.eddiep.minecraft.ls.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        return false;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public abstract String getName();
}