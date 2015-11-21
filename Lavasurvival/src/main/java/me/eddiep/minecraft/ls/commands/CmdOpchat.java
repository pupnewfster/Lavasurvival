package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.ranks.UserInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CmdOpchat extends Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        String message = "";
        if (args.length > 0)
            for (String arg : args)
                message += arg + " ";
        message = message.trim();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            UserInfo u = um.getUser(p.getUniqueId());
            if (args.length > 0)
                sendOps(p.getUniqueId(), message);
            else if (!u.isInOpChat()) {
                p.sendMessage(ChatColor.GOLD + "You are now sending messages only to ops.");
                u.toggleOpChat();
            } else {
                p.sendMessage(ChatColor.GOLD + "You are no longer sending messages to ops.");
                u.toggleOpChat();
            }
        } else {
            if (args.length == 0)
                sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "The console cannot toggle opchat.");
            else
                consoleToOps(message);
        }
        return true;
    }

    private void sendOps(UUID uuid, String message) {
        String send = ChatColor.GOLD + "To Ops - " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', um.getUser(uuid).getRank().getTitle()) +
                Bukkit.getPlayer(uuid).getDisplayName() + ": " + ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.broadcast(send, "ls.opchat");
        Bukkit.getConsoleSender().sendMessage(send);
    }

    private void consoleToOps(String message) {
        String send = ChatColor.GOLD + "To Ops - Console" + ChatColor.WHITE + " " + ChatColor.translateAlternateColorCodes('&', message.trim());
        Bukkit.broadcast(send, "ls.opchat");
        Bukkit.getConsoleSender().sendMessage(send);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public String getName() {
        return "opchat";
    }
}