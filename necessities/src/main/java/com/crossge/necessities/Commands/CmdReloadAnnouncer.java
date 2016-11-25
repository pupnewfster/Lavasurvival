package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;

public class CmdReloadAnnouncer implements Cmd {
    @Override
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        sender.sendMessage(var.getMessages() + "Reloading Announcer.");
        Necessities.getInstance().getAnnouncer().reloadAnnouncer();
        sender.sendMessage(var.getMessages() + "Announcer reloaded.");
        return true;
    }
}