package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdKick implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to kick and a reason.");
            return true;
        }
        UUID uuid = Utils.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player target = Bukkit.getPlayer(uuid);
        String name = Necessities.getConsole().getName().replaceAll(":", "");
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (target.hasPermission("Necessities.antiKick") && !p.hasPermission("Necessities.kickany")) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You may not kick someone who has Necessities.antiKick unless you have Necessities.kickany.");
                return true;
            }
            name = p.getName();
        }
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++)
            reasonBuilder.append(args[i]).append(" ");
        String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString().trim());
        Bukkit.broadcastMessage(var.getMessages() + name + " kicked " + var.getObj() + target.getName() + (reason.equals("") ? "" : var.getMessages() + " for " + var.getObj() + reason));
        target.kickPlayer(reason);
        return true;
    }
}