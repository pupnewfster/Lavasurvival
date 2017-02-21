package com.crossge.necessities.Commands;

import com.crossge.necessities.Console;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdSlack implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        String message = "";
        if (args.length > 0) {
            StringBuilder messageBuilder = new StringBuilder();
            for (String arg : args)
                messageBuilder.append(arg).append(" ");
            message = messageBuilder.toString().trim();
        }
        Variables var = Necessities.getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = Necessities.getUM().getUser(p.getUniqueId());
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
        YamlConfiguration config = Necessities.getInstance().getConfig();
        Player player = Bukkit.getPlayer(uuid);
        String send = ChatColor.translateAlternateColorCodes('&', config.getString("Necessities.ChatFormat"));
        send = Necessities.getVar().getMessages() + "To Slack - " + ChatColor.WHITE + send;
        send = send.replaceAll("\\{WORLD} ", "");
        send = send.replaceAll("\\{TITLE} ", "");
        send = send.replaceAll("\\{RANK}", ChatColor.translateAlternateColorCodes('&', Necessities.getUM().getUser(uuid).getRank().getTitle()));
        send = send.replaceAll("\\{NAME}", player.getDisplayName());
        send = send.replaceAll("\\{MESSAGE}", "");
        if (player.hasPermission("Necessities.colorchat"))
            message = ChatColor.translateAlternateColorCodes('&', (player.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", "")));
        Bukkit.broadcast(send + message, "Necessities.slack");
        Necessities.getSlack().sendMessage(send.replaceFirst("To Slack - ", "") + message);
    }

    private void consoleToSlack(String message) {
        Console console = Necessities.getConsole();
        String send = Necessities.getVar().getMessages() + "To Slack - " + console.getName() + ChatColor.WHITE + " " + ChatColor.translateAlternateColorCodes('&', message.trim());
        Bukkit.broadcast(send, "Necessities.slack");
        Necessities.getSlack().sendMessage(console.getName() + " " + ChatColor.translateAlternateColorCodes('&', message.trim()));
    }
}