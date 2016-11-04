package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdGamemode implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Must enter a gamemode.");
            return true;
        }
        if (sender instanceof Player && args.length == 1) {
            GameMode gamemode = getGM(args[0]);
            ((Player) sender).setGameMode(gamemode);
            sender.sendMessage(var.getMessages() + "Set gamemode to " + var.getObj() + gamemode.toString().toLowerCase());
            return true;
        }
        UUID uuid = Necessities.getInstance().getUUID().getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player p = Bukkit.getPlayer(uuid);
        GameMode gamemode = getGM(args[1]);
        p.setGameMode(gamemode);
        sender.sendMessage(var.getMessages() + "Set gamemode for " + var.getObj() + p.getDisplayName() + var.getMessages() + " to " + var.getObj() +
                gamemode.toString().toLowerCase());
        return true;
    }

    private GameMode getGM(String message) {
        if (message.equalsIgnoreCase("adventure") || message.equalsIgnoreCase("2") || message.equalsIgnoreCase("adv"))
            return GameMode.ADVENTURE;
        if (message.equalsIgnoreCase("creative") || message.equalsIgnoreCase("1") || message.equalsIgnoreCase("c"))
            return GameMode.CREATIVE;
        if (message.equalsIgnoreCase("spectator") || message.equalsIgnoreCase("3"))
            return GameMode.SPECTATOR;
        return GameMode.SURVIVAL;
    }
}