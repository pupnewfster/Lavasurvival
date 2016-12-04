package com.crossge.necessities.Commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Cmd {
    @SuppressWarnings("SameReturnValue")
    boolean commandUse(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}