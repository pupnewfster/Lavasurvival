package com.crossge.necessities.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdToggleChat implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player)
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot use this command, it is console specific.");
        else {
            sender.sendMessage(var.getMessages() + (console.chatToggled() ? "Toggled back to command mode." : "Toggled to chat mode."));
            console.chatToggle();
        }
        return true;
    }
}