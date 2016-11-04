package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTop implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location top = p.getLocation();
            double topy = 0;
            for (int i = 0; i < top.getWorld().getMaxHeight(); i++)
                if ((new Location(top.getWorld(), top.getX(), i, top.getZ())).getBlock().getType().isSolid())
                    topy = i + 1;
            p.teleport(new Location(top.getWorld(), top.getX(), topy, top.getZ(), top.getYaw(), top.getPitch()));
            p.sendMessage(var.getMessages() + "Teleporting to top.");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console cannot use /top soz.");
        return true;
    }
}