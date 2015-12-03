package com.crossge.necessities.Commands;

import com.crossge.necessities.Hats.Hat;
import com.crossge.necessities.Hats.HatType;
import com.crossge.necessities.RankManager.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdHat extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = um.getUser(p.getUniqueId());
            if (args.length == 0) {
                if (u.getHat() == null) {
                    p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid hat type.");
                    p.sendMessage(var.getMessages() + validTypes());
                } else {
                    u.setHat(null);
                    p.sendMessage(var.getMessages() + "You are no longer wearing a hat.");
                }
                return true;
            }
            HatType type = HatType.fromString(args[0]);
            if (type == null) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid hat type.");
                p.sendMessage(var.getMessages() + validTypes());
                return true;
            }
            Hat h = Hat.fromType(type, p.getLocation());
            if (h == null) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid hat type.");
                p.sendMessage(var.getMessages() + validTypes());
                return true;
            }
            u.setHat(h);
            p.sendMessage(var.getMessages() + "You are now wearing a " + var.getObj() + type.getName().toLowerCase().replaceAll("_", " ") + var.getMessages() + ".");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must be a player to wear a hat.");
        return true;
    }

    private String validTypes() {
        String types = "Valid hat types: ";
        for (String h : HatType.getTypes())
            types += h.toLowerCase() + ", ";
        return types.substring(0, types.length() - 2) + ".";
    }
}