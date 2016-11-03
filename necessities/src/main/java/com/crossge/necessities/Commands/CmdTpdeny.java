package com.crossge.necessities.Commands;

import com.crossge.necessities.Teleports;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdTpdeny implements Cmd {
    private Teleports tps = new Teleports();
    private CmdHide hide = new CmdHide();

    public boolean commandUse(CommandSender sender, String[] args) {
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
                uuid = get.getID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            Player target = sender.getServer().getPlayer(uuid);
            if (!p.hasPermission("Necessities.seehidden") && hide.isHidden(target)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
                return true;
            }
            if (!tps.hasRequestFrom(p.getUniqueId(), uuid)) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You do not have any pending teleport request from that player.");
                return true;
            }
            tps.removeRequestFrom(p.getUniqueId(), uuid);
            target.sendMessage(var.getObj() + p.getName() + var.getMessages() + " denied your teleport request");
            p.sendMessage(var.getMessages() + "Teleport request denied.");
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You cannot teleport so are unable to get teleport requests.");
        return true;
    }
}