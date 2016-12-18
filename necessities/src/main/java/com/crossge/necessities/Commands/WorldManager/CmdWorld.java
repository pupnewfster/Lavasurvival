package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdWorld implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a world name.");
            return true;
        }
        if (sender instanceof Player) {
            World dim = sender.getServer().getWorld(args[0]);
            if (args.length > 1)
                dim = sender.getServer().getWorld(args[1]);
            if (dim == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid world.");
                return true;
            }
            if (args.length > 1) {
                UUID uuid = Necessities.getUUID().getID(args[0]);
                if (uuid == null) {
                    sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                    return true;
                }
                Bukkit.getPlayer(uuid).teleport(dim.getSpawnLocation());
                sender.sendMessage(var.getMessages() + "Teleported " + var.getObj() + Bukkit.getPlayer(uuid).getName() + var.getMessages() + " to " + var.getObj() + dim.getName() + var.getMessages() + ".");
                return true;
            }
            ((Player) sender).teleport(dim.getSpawnLocation());
            sender.sendMessage(var.getMessages() + "Teleported to " + var.getObj() + dim.getName() + var.getMessages() + ".");
        } else {
            if (args.length > 1) {
                World dim = sender.getServer().getWorld(args[1]);
                UUID uuid = Necessities.getUUID().getID(args[0]);
                if (uuid == null) {
                    sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                    return true;
                }
                Bukkit.getPlayer(uuid).teleport(dim.getSpawnLocation());
                sender.sendMessage(var.getMessages() + "Teleported " + var.getObj() + Bukkit.getPlayer(uuid).getName() + var.getMessages() + " to " + var.getObj() + dim.getName() + var.getMessages() + ".");
                return true;
            }
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You can not teleport to other worlds because you are not a player.");
        }
        return true;
    }
}