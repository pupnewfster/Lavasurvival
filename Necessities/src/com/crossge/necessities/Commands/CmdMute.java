package com.crossge.necessities.Commands;

import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdMute extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to mute.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        User u = um.getUser(uuid);
        String name = "The console";
        if (sender instanceof Player)
            name = ((Player) sender).getDisplayName();
        if (!u.isMuted()) {
            Bukkit.broadcastMessage(var.getObj() + name + var.getMessages() + " muted " + var.getObj() + u.getPlayer().getDisplayName() + var.getMessages() + ".");
            u.getPlayer().sendMessage(var.getDemote() + "You have been " + var.getObj() + "muted" + var.getMessages() + ".");
        } else {
            Bukkit.broadcastMessage(var.getObj() + name + var.getMessages() + " unmuted " + var.getObj() + u.getPlayer().getDisplayName() + var.getMessages() + ".");
            u.getPlayer().sendMessage(var.getPromote() + "You have been " + var.getObj() + "unmuted" + var.getMessages() + ".");
        }
        u.setMuted(!u.isMuted());
        return true;
    }
}