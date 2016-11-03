package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdTpahere implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Format requires you enter a player to send a teleport request to.");
                return true;
            }
            UUID uuid = get.getID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            Player target = sender.getServer().getPlayer(uuid);
            Player p = (Player) sender;
            if (p.getLocation().getBlock().hasMetadata("classic_block") || p.getEyeLocation().getBlock().hasMetadata("classic_block") ||
                    target.getLocation().getBlock().hasMetadata("classic_block") || target.getEyeLocation().getBlock().hasMetadata("classic_block")) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You or the target are in the lava.");
                return true;
            }
            String tPrefix = um.getUser(uuid).getStatus(), pPrefix = um.getUser(p.getUniqueId()).getStatus();
            if (!p.hasPermission("Necessities.seehidden") && Necessities.getInstance().getHide().isHidden(target)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            } else if (!tPrefix.equals(pPrefix)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must both be alive or dead to tpa.");
                return true;
            }
            if (p.getName().equals(target.getName())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not send teleport requests to yourself.");
                return true;
            }
            User u = um.getUser(uuid);
            User self = um.getUser(p.getUniqueId());
            if (u.isIgnoring(p.getUniqueId())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That user is ignoring you, so you cannot send them a teleport request.");
                return true;
            }
            if (self.isIgnoring(uuid)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are ignoring that user, so cannot send them a teleport request.");
                return true;
            }
            Necessities.getInstance().getTPs().addRequest(uuid, p.getUniqueId().toString() + " toThem");
            target.sendMessage(var.getObj() + p.getName() + var.getMessages() + " has requested that you teleport to them.");
            target.sendMessage(var.getMessages() + "To teleport, type " + var.getObj() + "/tpaccept" + var.getMessages() + ".");
            target.sendMessage(var.getMessages() + "To deny this request, type " + var.getObj() + "/tpdeny" + var.getMessages() + ".");
            p.sendMessage(var.getMessages() + "Request sent to " + var.getObj() + target.getName() + var.getMessages() + ".");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot teleport so are unable to request someone to teleport to you.");
        return true;
    }
}