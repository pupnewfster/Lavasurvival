package com.crossge.necessities.Commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSuicide extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                player.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot suicide, you already died.");
                return true;
            }
            player.setHealth(0);
            player.sendMessage(var.getMessages() + "Goodbye cruel world.");
            Bukkit.broadcastMessage(var.getObj() + player.getDisplayName() + var.getMessages() + " took their own life.");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot suicide you would kill the server.");
        return true;
    }
}