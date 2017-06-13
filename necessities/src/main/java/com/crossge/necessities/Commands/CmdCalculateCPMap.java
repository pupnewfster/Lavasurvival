package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.Variables;
import me.eddiep.handles.PhysicsEngine;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCalculateCPMap implements Cmd {
    @Override
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (sender instanceof Player) {
            User u = Necessities.getUM().getUser(((Player) sender).getUniqueId());
            Location left = u.getLeft();
            Location right = u.getRight();
            if (!left.getWorld().equals(right.getWorld())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Please select left and right corners of map.");
                return true;
            }
            if (!PhysicsEngine.calculateMeltMap(left, right))
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Map mapping failed.");
            else
                sender.sendMessage(var.getMessages() + "Map mapping complete.");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Login to perform this command because you have to click two locations.");
        return true;
    }
}