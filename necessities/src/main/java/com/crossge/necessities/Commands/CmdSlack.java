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

import java.io.File;
import java.util.UUID;

public class CmdSlack implements Cmd {
    private File configFile = new File("plugins/Necessities", "config.yml");

    public boolean commandUse(CommandSender sender, String[] args) {
        String message = "";
        if (args.length > 0)
            for (String arg : args)
                message += arg + " ";
        message = message.trim();
        Variables var = Necessities.getInstance().getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = Necessities.getInstance().getUM().getUser(p.getUniqueId());
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
        send = Necessities.getInstance().getVar().getMessages() + "To Slack - " + ChatColor.WHITE + send;
        send = send.replaceAll("\\{WORLD\\} ", "");
        send = send.replaceAll("\\{GUILD\\} ", "");
        send = send.replaceAll("\\{TITLE\\} ", "");
        send = send.replaceAll("\\{RANK\\}", ChatColor.translateAlternateColorCodes('&', Necessities.getInstance().getUM().getUser(uuid).getRank().getTitle()));
        send = send.replaceAll("\\{NAME\\}", player.getDisplayName());
        send = send.replaceAll("\\{MESSAGE\\}", "");
        if (player.hasPermission("Necessities.colorchat"))
            message = ChatColor.translateAlternateColorCodes('&', (player.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", "")));
        Bukkit.broadcast(send + message, "Necessities.slack");
        Necessities.getInstance().getSlack().sendMessage(send.replaceFirst("To Slack - ", "") + message);
    }

    private void consoleToSlack(String message) {
        Console console = Necessities.getInstance().getConsole();
        String send = Necessities.getInstance().getVar().getMessages() + "To Slack - " + console.getName() + ChatColor.WHITE + " " + ChatColor.translateAlternateColorCodes('&', message.trim());
        Bukkit.broadcast(send, "Necessities.slack");
        Necessities.getInstance().getSlack().sendMessage(console.getName() + " " + ChatColor.translateAlternateColorCodes('&', message.trim()));
    }
}