package com.crossge.necessities.Commands;

import com.crossge.necessities.Janet.JanetSlack;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRequestMod implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        String reason = "";
        if (args.length > 0)
            for (String arg : args)
                reason += arg + " ";
        reason = reason.trim();
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = Necessities.getInstance().getUM().getUser(p.getUniqueId());
            if (System.currentTimeMillis() - u.getLastRequest() < 300000 && System.currentTimeMillis() - u.getLastRequest() > 0) {
                p.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You requested a mod to recently, please wait a little while and try again.");
                return true;
            }
            JanetSlack slack = Necessities.getInstance().getSlack();
            if (args.length > 0) {
                slack.sendMessage(p.getName() + " requested a mod with the reason: " + reason);
                Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', p.getName() + " requested a mod with the reason: " + reason), "Necessities.slack");
            } else {
                slack.sendMessage(p.getName() + " requested a mod.");
                Bukkit.broadcast(var.getMessages() + "To Slack - " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', p.getName() + " requested a mod."), "Necessities.slack");
            }
            p.sendMessage(var.getMessages() + "Request successfully sent.");
            u.setLastRequest(System.currentTimeMillis());
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You do not need a moderator.");
        return true;
    }
}