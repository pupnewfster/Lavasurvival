package com.crossge.necessities.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdToggleChat extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player)
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot use this command, it is console specific.");
        else {
            if (console.chatToggled())
                sender.sendMessage(var.getMessages() + "Toggled back to command mode.");
            else
                sender.sendMessage(var.getMessages() + "Toggled to chat mode.");
            console.chatToggle();
        }
        return true;
    }
}