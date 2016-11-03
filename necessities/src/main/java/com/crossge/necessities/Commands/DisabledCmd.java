package com.crossge.necessities.Commands;

import org.bukkit.command.CommandSender;

public class DisabledCmd implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        sender.sendMessage("Unknown command. Type \"/help\" for help.");
        return true;
    }
}