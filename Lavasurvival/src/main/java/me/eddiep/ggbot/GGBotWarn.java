package me.eddiep.ggbot;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GGBotWarn {//Based off of Janet
    private static HashMap<UUID,Integer> warnCount = new HashMap<UUID,Integer>();
    private int warns = 3;
    private static String ggbotName = ChatColor.DARK_GREEN + "GGBot: " + ChatColor.WHITE;

    public void removePlayer(UUID uuid) {
        warnCount.remove(uuid);
    }

    public void warn(UUID uuid, String reason) {
        if(!warnCount.containsKey(uuid))
            warnCount.put(uuid, 1);
        else
            warnCount.put(uuid, warnCount.get(uuid) + 1);
        if(warnCount.get(uuid) == warns) {
            if(reason.equals("Language"))
                language(uuid);
            else if(reason.equals("ChatSpam"))
                chatSpam(uuid);
            else if(reason.equals("Adds"))
                advertising(uuid);
        } else {
            if(reason.equals("Language"))
                langMsg(uuid);
            else if(reason.equals("ChatSpam"))
                chatMsg(uuid);
            else if(reason.equals("Adds"))
                addsMsg(uuid);
            timesLeft(uuid);
        }
    }

    private void timesLeft(UUID uuid) {
        String left = Integer.toString(warns - warnCount.get(uuid));
        String plural = "time";
        if(warns - warnCount.get(uuid) != 1)
            plural += "s";
        Bukkit.getPlayer(uuid).sendMessage(ggbotName + "Do it " + left + " more " + plural + " and you will be kicked.");
    }

    private void langMsg(UUID uuid) {
        broadcast(ggbotName + getName(uuid) + " was warned for using bad language.", uuid);
        Bukkit.getPlayer(uuid).sendMessage(ggbotName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not swear please.");
    }

    private void chatMsg(UUID uuid) {
        broadcast(ggbotName + getName(uuid) + " was warned for spamming the chat.", uuid);
        Bukkit.getPlayer(uuid).sendMessage(ggbotName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not spam the chat please.");
    }

    private void addsMsg(UUID uuid) {
        broadcast(ggbotName + getName(uuid) + " was warned for advertising other servers.", uuid);
        Bukkit.getPlayer(uuid).sendMessage(ggbotName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not advertise other servers please.");
    }

    private void chatSpam(UUID uuid) {
        broadcast(ggbotName + getName(uuid) + " was kicked for spamming the chat.", uuid);
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Don't spam the chat!");
    }

    private void language(UUID uuid) {
        broadcast(ggbotName + getName(uuid) + " was kicked for using bad language.", uuid);
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Watch your language!");
    }

    private void advertising(UUID uuid) {
        broadcast(ggbotName + getName(uuid) + " was kicked for advertising.", uuid);
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Do not advertise other servers!");
    }

    private String getName(UUID uuid) {
        return Bukkit.getPlayer(uuid).getName();
    }

    private void broadcast(String message, UUID uuid) {
        Bukkit.getConsoleSender().sendMessage(message);
        for(Player p : Bukkit.getOnlinePlayers())
            if(p.getUniqueId() != uuid)
                p.sendMessage(message);
    }
}