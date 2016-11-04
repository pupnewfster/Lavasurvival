package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import com.crossge.necessities.WorldManager.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdWarps implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        WarpManager warps = Necessities.getInstance().getWarps();
        sender.sendMessage((warps.getWarps().equals("") ? var.getEr() + "Error: " + var.getErMsg() + "There are no warps set." : var.getMessages() + "Available warps: " + ChatColor.WHITE + warps.getWarps()));
        return true;
    }
}