package com.crossge.necessities.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdSlap extends Cmd {
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
        Player target = sender.getServer().getPlayer(uuid);
        Location loc = new Location(target.getWorld(), target.getLocation().getBlockX(), 2500, target.getLocation().getBlockZ());
        target.teleport(loc);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Bukkit.broadcastMessage(var.getMessages() + target.getName() + " was slapped sky high by " + player.getName());
        } else
            Bukkit.broadcastMessage(var.getMessages() + target.getName() + " was slapped sky high by " + console.getName().replaceAll(":", ""));
        return true;
    }
}