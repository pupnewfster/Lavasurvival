package com.crossge.necessities.Commands;

import com.crossge.necessities.Formatter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTppos extends Cmd {
    Formatter form = new Formatter();

    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you enter an x coordinate " +
                    "a y coordinate, and a z coordinate  to teleport to.");
            return true;
        }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            World dim = p.getWorld();
            if (!form.isLegal(args[0])) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid x coordinate.");
                return true;
            }
            if (!form.isLegal(args[1])) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid y coordinate.");
                return true;
            }
            if (!form.isLegal(args[2])) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid z coordinate.");
                return true;
            }
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            Location loc = new Location(dim, x, y, z);
            if (args.length > 4) {
                if (!form.isLegal(args[3])) {
                    sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid yaw.");
                    return true;
                }
                if (!form.isLegal(args[4])) {
                    sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid pitch.");
                    return true;
                }
                int yaw = Integer.parseInt(args[3]);
                int pitch = Integer.parseInt(args[4]);
                loc = new Location(dim, x, y, z, yaw, pitch);
            }
            p.teleport(loc);
            p.sendMessage(var.getMessages() + "Teleporting...");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must be a player to teleport to a location.");
        return true;
    }
}