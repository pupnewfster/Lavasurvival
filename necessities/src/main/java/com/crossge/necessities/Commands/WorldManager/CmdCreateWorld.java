package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;

public class CmdCreateWorld implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a worldname.");
            return true;
        }
        if (wm.worldExists(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world already exists.");
            return true;
        }
        if (wm.worldUnloaded(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world already exists just use /loadworld.");
            return true;
        }
        World.Environment enviro = World.Environment.NORMAL;
        if (args.length > 1)
            wm.getEnviroment(args[1]);//would check if valid... but it auto changes to normal if not so that works fine I guess
        String generator = null;
        if (args.length > 2 && !args[2].equalsIgnoreCase("null"))
            generator = args[2];//TODO: Check if valid generator somehow
        WorldType type = WorldType.NORMAL;
        if (args.length > 3)
            type = wm.getType(args[3]);
        wm.createWorld(args[0], enviro, generator, type);
        sender.sendMessage(var.getMessages() + "Created world named " + var.getObj() + args[0] + var.getMessages() + ".");
        return true;
    }
}