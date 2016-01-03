package com.crossge.necessities.Commands;

import com.crossge.necessities.Janet.JanetSlack;
import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRequestMod extends Cmd {
    private JanetSlack slack = new JanetSlack();

    public boolean commandUse(CommandSender sender, String[] args) {
        String reason = "";
        if (args.length > 0)
            for (String arg : args)
                reason += arg + " ";
        reason = reason.trim();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = um.getUser(p.getUniqueId());
            if (System.currentTimeMillis() - u.getLastRequest() < 300000 && System.currentTimeMillis() - u.getLastRequest() > 0) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You requested a mod to recently, please wait a little while and try again.");
                return true;
            }
            if (args.length > 0) {
                slack.sendMessage(ChatColor.stripColor(p.getName() + " requested a mod. With the reason " + reason));
                Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', p.getName() + " requested a mod. With the reason " + reason), "Necessities.slack");
            } else {
                slack.sendMessage(ChatColor.stripColor(p.getName() + " requested a mod."));
                Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', p.getName() + " requested a mod."), "Necessities.slack");
            }
            u.setLastRequest(System.currentTimeMillis());
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You do not need a moderator.");
        return true;
    }
}