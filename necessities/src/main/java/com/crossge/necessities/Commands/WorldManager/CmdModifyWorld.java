package com.crossge.necessities.Commands.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

public class CmdModifyWorld implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you enter a worldname a setting to modify and a value to change the "
                    + "setting to.");
            sender.sendMessage(var.getMessages() + "Valid settings are: " + ChatColor.WHITE + "difficulty, gamemode, structures, pvp, animals, and monsters.");
            return true;
        }
        if (!wm.worldExists(args[0])) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That world does not exist.");
            return true;
        }
        String worldName = Bukkit.getWorld(args[0]).getName();
        if (args[1].equalsIgnoreCase("difficulty")) {
            Difficulty dif = wm.getDifficulty(args[2]);
            wm.setSetting(worldName, "difficulty", dif.toString());
            Bukkit.getWorld(args[0]).setDifficulty(dif);
            sender.sendMessage(var.getMessages() + "Set difficulty to " + var.getObj() + dif.toString().toLowerCase() + var.getMessages() + " in world " +
                    var.getObj() + worldName);
        } else if (args[1].equalsIgnoreCase("gamemode")) {
            GameMode gm = getGM(args[2]);
            wm.setSetting(worldName, "gamemode", gm.toString());
            sender.sendMessage(var.getMessages() + "Set default gamemode in world " + var.getObj() + worldName + var.getMessages() + " to " + var.getObj() +
                    gm.toString().toLowerCase());
        } else if (args[1].equalsIgnoreCase("structures")) {
            boolean value;
            if (args[2].equalsIgnoreCase("true"))
                value = true;
            else if (args[2].equalsIgnoreCase("false"))
                value = false;
            else {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must either enter true or false as the value.");
                return true;
            }
            wm.setSetting(worldName, "structures", value);//TODO: this doesn't actually set it without either reloading world or restarting server ._.
            sender.sendMessage(var.getMessages() + "Set structure generation in world " + var.getObj() + worldName + var.getMessages() + " to " + var.getObj() +
                    Boolean.toString(value));
        } else if (args[1].equalsIgnoreCase("pvp")) {
            boolean value;
            if (args[2].equalsIgnoreCase("true"))
                value = true;
            else if (args[2].equalsIgnoreCase("false"))
                value = false;
            else {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must either enter true or false as the value.");
                return true;
            }
            wm.setSetting(worldName, "pvp", value);
            Bukkit.getWorld(worldName).setPVP(value);
            sender.sendMessage(var.getMessages() + "Set pvp in world " + var.getObj() + worldName + var.getMessages() + " to " + var.getObj() +
                    Boolean.toString(value));
        } else if (args[1].equalsIgnoreCase("animals")) {
            boolean value;
            if (args[2].equalsIgnoreCase("true"))
                value = true;
            else if (args[2].equalsIgnoreCase("false"))
                value = false;
            else {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must either enter true or false as the value.");
                return true;
            }
            wm.setSetting(worldName, "spawning.animals", value);
            Bukkit.getWorld(worldName).setSpawnFlags(Bukkit.getWorld(worldName).getAllowMonsters(), value);
            sender.sendMessage(var.getMessages() + "Set animal spawning in world " + var.getObj() + worldName + var.getMessages() + " to " + var.getObj() +
                    Boolean.toString(value));
        } else if (args[1].equalsIgnoreCase("monsters")) {
            boolean value;
            if (args[2].equalsIgnoreCase("true"))
                value = true;
            else if (args[2].equalsIgnoreCase("false"))
                value = false;
            else {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must either enter true or false as the value.");
                return true;
            }
            wm.setSetting(worldName, "spawning.monsters", value);
            Bukkit.getWorld(worldName).setSpawnFlags(value, Bukkit.getWorld(worldName).getAllowAnimals());
            sender.sendMessage(var.getMessages() + "Set monster spawning in world " + var.getObj() + worldName + var.getMessages() + " to " + var.getObj() +
                    Boolean.toString(value));
        } else {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid setting.");
            sender.sendMessage(var.getMessages() + "Valid settings are: " + ChatColor.WHITE + "difficulty, gamemode, structures, pvp, animals, and monsters.");
        }
        return true;
    }

    private GameMode getGM(String message) {
        if (message.equalsIgnoreCase("adventure") || message.equalsIgnoreCase("2") || message.equalsIgnoreCase("adv"))
            return GameMode.ADVENTURE;
        if (message.equalsIgnoreCase("creative") || message.equalsIgnoreCase("1") || message.equalsIgnoreCase("classic"))
            return GameMode.CREATIVE;
        return GameMode.SURVIVAL;
    }
}