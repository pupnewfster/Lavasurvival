package com.crossge.necessities.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdSlap implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to slap.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player target = Bukkit.getPlayer(uuid);
        Location loc = target.getLocation().clone().add(0, 2500, 0);
        target.teleport(loc);
        Bukkit.broadcastMessage(var.getMessages() + target.getName() + " was slapped sky high by " + (sender instanceof Player ? sender.getName() : console.getName().replaceAll(":", "")));
        return true;
    }
}