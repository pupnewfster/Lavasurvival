package com.crossge.necessities.Commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public class DisabledCmd extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        sender.sendMessage("Unkown command. Type \"/help\" for help.");
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}