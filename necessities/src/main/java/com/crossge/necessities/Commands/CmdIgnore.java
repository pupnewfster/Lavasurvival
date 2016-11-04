package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdIgnore implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to ignore.");
                return true;
            }
            UUID uuid = Necessities.getInstance().getUUID().getID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            if (uuid.equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not ignore yourself.");
                return true;
            }
            Player p = (Player) sender;
            UserManager um = Necessities.getInstance().getUM();
            User self = um.getUser(p.getUniqueId());
            User u = um.getUser(uuid);
            if (u.getPlayer().hasPermission("Necessities.unignoreable")) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player cannot be ignored.");
                return true;
            }
            Player t = Bukkit.getPlayer(uuid);
            if (self.isIgnoring(uuid)) {
                self.unignore(uuid);
                sender.sendMessage(var.getMessages() + "No longer ignoring " + var.getObj() + t.getDisplayName() + var.getMessages() + ".");
            } else {
                self.ignore(uuid);
                sender.sendMessage(var.getMessages() + "Ignoring " + var.getObj() + t.getDisplayName() + var.getMessages() + ".");
            }
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot ignore anyone if you feel strongly about it try muting them.");
        return true;
    }
}