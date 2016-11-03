package com.crossge.necessities.Commands;

import com.crossge.necessities.Janet.JanetSlack;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class CmdSlack implements Cmd {
    private File configFile = new File("plugins/Necessities", "config.yml");
    private JanetSlack slack = Necessities.getInstance().getSlack();

    public boolean commandUse(CommandSender sender, String[] args) {
        String message = "";
        if (args.length > 0)
            for (String arg : args)
                message += arg + " ";
        message = message.trim();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = um.getUser(p.getUniqueId());
            if (args.length > 0)
                sendSlack(p.getUniqueId(), message);
            else {
                p.sendMessage(var.getMessages() + "You are " + (!u.slackChat() ? "now" : "no longer") + " sending messages to slack.");
                u.toggleSlackChat();
            }
        } else if (args.length == 0)
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console cannot toggle slack chat.");
        else
            consoleToSlack(message);
        return true;
    }

    private void sendSlack(UUID uuid, String message) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        Player player = Bukkit.getPlayer(uuid);
        String send = ChatColor.translateAlternateColorCodes('&', config.getString("Necessities.ChatFormat"));
        send = var.getMessages() + "To Slack - " + ChatColor.WHITE + send;
        send = send.replaceAll("\\{WORLD\\} ", "");
        send = send.replaceAll("\\{GUILD\\} ", "");
        send = send.replaceAll("\\{TITLE\\} ", "");
        send = send.replaceAll("\\{RANK\\}", ChatColor.translateAlternateColorCodes('&', um.getUser(uuid).getRank().getTitle()));
        send = send.replaceAll("\\{NAME\\}", player.getDisplayName());
        send = send.replaceAll("\\{MESSAGE\\}", "");
        if (player.hasPermission("Necessities.colorchat"))
            message = ChatColor.translateAlternateColorCodes('&', (player.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", "")));
        Bukkit.broadcast(send + message, "Necessities.slack");
        slack.sendMessage(send.replaceFirst("To Slack - ", "") + message);
    }

    private void consoleToSlack(String message) {
        String send = var.getMessages() + "To Slack - " + console.getName() + ChatColor.WHITE + " " + ChatColor.translateAlternateColorCodes('&', message.trim());
        Bukkit.broadcast(send, "Necessities.slack");
        slack.sendMessage(console.getName() + " " + ChatColor.translateAlternateColorCodes('&', message.trim()));
    }
}