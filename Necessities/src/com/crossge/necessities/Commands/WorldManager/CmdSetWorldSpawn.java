package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSetWorldSpawn extends WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location l = p.getLocation();
            wm.setWorldSpawn(l);
            p.sendMessage(var.getMessages() + "Set spawn for " + var.getObj() + l.getWorld().getName() + var.getMessages() + " at: " + var.getObj() +
                    l.getBlockX() + var.getMessages() + ", " + var.getObj() + l.getBlockY() + var.getMessages() + ", " + var.getObj() + l.getBlockZ());
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must be a player to go to set the spawn for the world you are in.");
        return true;
    }
}