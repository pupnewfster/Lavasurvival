package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Teleports;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdTpaccept implements Cmd {
    private final Teleports tps = Necessities.getInstance().getTPs();

    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            UUID uuid = null;
            if (args.length == 0) {
                uuid = tps.lastRequest(p.getUniqueId());
                if (uuid == null) {
                    sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You do not have any pending teleport requests.");
                    return true;
                }
            }
            if (args.length > 0)
                uuid = Necessities.getInstance().getUUID().getID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            Player target = sender.getServer().getPlayer(uuid);
            if (p.getLocation().getBlock().hasMetadata("classic_block") || p.getEyeLocation().getBlock().hasMetadata("classic_block") ||
                    target.getLocation().getBlock().hasMetadata("classic_block") || target.getEyeLocation().getBlock().hasMetadata("classic_block")) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You or the target are in the lava.");
                return true;
            }
            UserManager um = Necessities.getInstance().getUM();
            String tPrefix = um.getUser(uuid).getStatus(), pPrefix = um.getUser(p.getUniqueId()).getStatus();
            if (!p.hasPermission("Necessities.seehidden") && Necessities.getInstance().getHide().isHidden(target)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            } else if (!tPrefix.equals(pPrefix)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must both be alive or dead to tpa.");
                return true;
            }
            if (!tps.hasRequestFrom(p.getUniqueId(), uuid)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You do not have any pending teleport request from that player.");
                return true;
            }
            User u = um.getUser(uuid);
            User self = um.getUser(p.getUniqueId());
            if (u.isIgnoring(p.getUniqueId())) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That user is ignoring you, so you cannot accept their teleport request.");
                return true;
            }
            if (self.isIgnoring(uuid)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are ignoring that user, so cannot accept their teleport request.");
                return true;
            }
            String type = tps.getRequestType(p.getUniqueId(), uuid);
            tps.removeRequestFrom(p.getUniqueId(), uuid);
            target.sendMessage(var.getObj() + p.getName() + var.getMessages() + " accepted your teleport request");
            p.sendMessage(var.getMessages() + "Teleport request accepted.");
            if (type.equals("toMe"))
                u.getPlayer().teleport(p);
            else if (type.equals("toThem"))
                p.teleport(u.getPlayer());
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot teleport so are unable to get teleport requests.");
        return true;
    }
}