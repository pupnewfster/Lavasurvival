package com.crossge.necessities.Commands;

import com.crossge.necessities.Console;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdToggleChat implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player)
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot use this command, it is console specific.");
        else {
            Console console = Necessities.getInstance().getConsole();
            sender.sendMessage(var.getMessages() + (console.chatToggled() ? "Toggled back to command mode." : "Toggled to chat mode."));
            console.chatToggle();
        }
        return true;
    }
}