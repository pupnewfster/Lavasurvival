package com.crossge.necessities.Commands;

import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdMe extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        String msg = "";
        for (String s : args)
            msg += s + " ";
        msg = msg.trim();
        if (sender instanceof Player) {
            User self = um.getUser(((Player) sender).getUniqueId());
            if (self.isMuted()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are muted.");
                return true;
            }
            if (self.getPlayer().hasPermission("Necessities.colorchat"))
                msg = ChatColor.translateAlternateColorCodes('&', (self.getPlayer().hasPermission("Necessities.magicchat") ? msg : msg.replaceAll("&k", "")));
            sendMessage(self, msg);
        } else
            Bukkit.broadcastMessage(var.getMe() + "*" + console.getName().replaceAll(":", "") + var.getMe() + msg);
        return true;
    }

    private void sendMessage(User sender, String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            User u = um.getUser(p.getUniqueId());
            if (u != null && !u.isIgnoring(sender.getUUID()))
                p.sendMessage(var.getMe() + "*" + sender.getRank().getColor() + sender.getPlayer().getDisplayName() + var.getMe() + " " + msg);
        }
        Bukkit.getConsoleSender().sendMessage(var.getMe() + "*" + sender.getRank().getColor() + sender.getPlayer().getDisplayName() + var.getMe() + " " + msg);
    }
}