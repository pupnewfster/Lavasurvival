package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CmdDevs implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        StringBuilder d = new StringBuilder(var.getMessages() + "The Devs for Necessities are: ");
        List<Necessities.DevInfo> devs = Necessities.getInstance().getDevs();
        for (int i = 0; i < devs.size(); i++)
            d.append(i + 1 >= devs.size() ? "and " + ChatColor.WHITE + ChatColor.ITALIC + devs.get(i).getName() + var.getMessages() + "." : "" + ChatColor.WHITE + ChatColor.ITALIC + devs.get(i).getName() + var.getMessages() + ", ");
        sender.sendMessage(d.toString());
        return true;
    }
}