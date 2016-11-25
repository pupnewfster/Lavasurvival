package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdOpChat implements Cmd {
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
                sendOps(p.getUniqueId(), message);
            else {
                p.sendMessage(var.getMessages() + "You are " + (!u.opChat() ? "now" : "no longer") + " sending messages only to ops.");
                u.toggleOpChat();
            }
        } else if (args.length == 0)
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console cannot toggle opchat.");
        else
            consoleToOps(message);
        return true;
    }

    private void sendOps(UUID uuid, String message) {
        YamlConfiguration config = Necessities.getInstance().getConfig();
        Player player = Bukkit.getPlayer(uuid);
        String send = ChatColor.translateAlternateColorCodes('&', config.getString("Necessities.ChatFormat"));
        send = Necessities.getInstance().getVar().getMessages() + "To Ops - " + ChatColor.WHITE + send;
        send = send.replaceAll("\\{WORLD} ", "");
        send = send.replaceAll("\\{TITLE} ", "");
        send = send.replaceAll("\\{RANK}", ChatColor.translateAlternateColorCodes('&', Necessities.getInstance().getUM().getUser(uuid).getRank().getTitle()));
        send = send.replaceAll("\\{NAME}", player.getDisplayName());
        send = send.replaceAll("\\{MESSAGE}", "");
        if (player.hasPermission("Necessities.colorchat"))
            message = ChatColor.translateAlternateColorCodes('&', (player.hasPermission("Necessities.magicchat") ? message : message.replaceAll("&k", "")));
        Bukkit.broadcast(send + message, "Necessities.opBroadcast");
    }

    private void consoleToOps(String message) {
        Bukkit.broadcast(Necessities.getInstance().getVar().getMessages() + "To Ops - " + Necessities.getInstance().getConsole().getName() + ChatColor.WHITE + " " + ChatColor.translateAlternateColorCodes('&', message.trim()), "Necessities.opBroadcast");
    }
}