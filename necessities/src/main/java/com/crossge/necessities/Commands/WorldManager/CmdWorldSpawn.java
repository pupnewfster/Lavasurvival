package com.crossge.necessities.Commands.WorldManager;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdWorldSpawn implements WorldCmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.teleport(p.getWorld().getSpawnLocation());
            p.sendMessage(var.getMessages() + "Teleported to this world's spawn.");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must be a player to go to the world's spawn.");
        return true;
    }
}