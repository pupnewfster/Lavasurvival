package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdMe implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        String msg = "";
        for (String s : args)
            msg += s + " ";
        msg = msg.trim();
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            User self = Necessities.getInstance().getUM().getUser(((Player) sender).getUniqueId());
            if (self.isMuted()) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You are muted.");
                return true;
            }
            if (self.getPlayer().hasPermission("Necessities.colorchat"))
                msg = ChatColor.translateAlternateColorCodes('&', (self.getPlayer().hasPermission("Necessities.magicchat") ? msg : msg.replaceAll("&k", "")));
            sendMessage(self, msg);
        } else
            Bukkit.broadcastMessage(var.getMe() + "*" + Necessities.getInstance().getConsole().getName().replaceAll(":", "") + var.getMe() + msg);
        return true;
    }

    private void sendMessage(User sender, String msg) {
        Variables var = Necessities.getInstance().getVar();
        for (Player p : Bukkit.getOnlinePlayers()) {
            User u = Necessities.getInstance().getUM().getUser(p.getUniqueId());
            if (u != null && !u.isIgnoring(sender.getUUID()))
                p.sendMessage(var.getMe() + "*" + sender.getRank().getColor() + sender.getPlayer().getDisplayName() + var.getMe() + " " + msg);
        }
        Bukkit.getConsoleSender().sendMessage(var.getMe() + "*" + sender.getRank().getColor() + sender.getPlayer().getDisplayName() + var.getMe() + " " + msg);
    }
}