package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdTp implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you enter a player to teleport to.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player target = Bukkit.getPlayer(uuid);
        if (sender instanceof Player && args.length == 1) {
            Player p = (Player) sender;
            if (!p.hasPermission("Necessities.seehidden") && Necessities.getInstance().getHide().isHidden(target)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            p.teleport(target);
            p.sendMessage(var.getMessages() + "Teleporting...");
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you enter a player to teleport, and a player to teleport them to.");
            return true;
        }
        UUID uuidTo = get.getID(args[1]);
        if (uuidTo == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player targetTo = Bukkit.getPlayer(uuidTo);
        target.teleport(targetTo.getLocation());
        String name = (sender instanceof Player ? sender.getName() : console.getName().replaceAll(":", ""));
        target.sendMessage(var.getObj() + name + var.getMessages() + " teleported you to " + targetTo.getName() + ".");
        sender.sendMessage(var.getMessages() + "You teleported " + var.getObj() + target.getName() + var.getMessages() + " to " + var.getObj() + targetTo.getName() + var.getMessages() + ".");
        return true;
    }
}