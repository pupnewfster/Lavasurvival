package com.crossge.necessities.Janet;

import com.crossge.necessities.RankManager.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class JanetWarn {
    private static HashMap<UUID, Integer> warnCount = new HashMap<>();
    private static String JanetName = "";
    JanetLog log = new JanetLog();
    private File configFile = new File("plugins/Necessities", "config.yml");
    private YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    private int warns = config.getInt("Necessities.warns");

    public void initiate() {
        RankManager rm = new RankManager();
        String rank = "";
        if (!rm.getOrder().isEmpty())
            rank = ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ");
        JanetName = rank + "Janet" + ChatColor.DARK_RED + ": " + ChatColor.WHITE;
    }

    public void removePlayer(UUID uuid) {
        warnCount.remove(uuid);
    }

    public void warn(UUID uuid, String reason, String warner) {
        if (!warnCount.containsKey(uuid))
            warnCount.put(uuid, 1);
        else
            warnCount.put(uuid, warnCount.get(uuid) + 1);
        String warning;
        if (warnCount.get(uuid) == warns) {
            if (reason.equals("Language"))
                warning = language(uuid);
            else if (reason.equals("ChatSpam"))
                warning = chatSpam(uuid);
            else if (reason.equals("CmdSpam"))
                warning = cmdSpam(uuid);
            else if (reason.equals("Adds"))
                warning = advertising(uuid);
            else
                warning = other(uuid, reason);
        } else {
            if (reason.equals("Language"))
                warning = langMsg(uuid);
            else if (reason.equals("ChatSpam"))
                warning = chatMsg(uuid);
            else if (reason.equals("CmdSpam"))
                warning = cmdMsg(uuid);
            else if (reason.equals("Adds"))
                warning = addsMsg(uuid);
            else
                warning = warnMessage(uuid, reason, warner);
            timesLeft(uuid);
        }
        log.log("Janet: " + warning);
    }

    private void timesLeft(UUID uuid) {
        String left = Integer.toString(warns - warnCount.get(uuid));
        String plural = "times";
        if (warns - warnCount.get(uuid) == 1)
            plural = "time";
        Bukkit.getPlayer(uuid).sendMessage(JanetName + "Do it " + left + " more " + plural + " and you will be kicked.");
    }

    private String langMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(JanetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not swear please.");
        broadcast(JanetName + getName(uuid) + " was warned for using bad language.", uuid);
        return getName(uuid) + " was warned for using bad language.";
    }

    private String chatMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(JanetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not spam the chat please.");
        broadcast(JanetName + getName(uuid) + " was warned for spamming the chat.", uuid);
        return getName(uuid) + " was warned for spamming the chat.";
    }

    private String cmdMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(JanetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not spam commands please.");
        broadcast(JanetName + getName(uuid) + " was warned for spamming commands.", uuid);
        return getName(uuid) + " was warned for spamming commands.";
    }

    private String addsMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(JanetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not advertise other servers please.");
        broadcast(JanetName + getName(uuid) + " was warned for advertising other servers.", uuid);
        return getName(uuid) + " was warned for advertising other servers.";
    }

    private String warnMessage(UUID uuid, String reason, String warner) {
        reason = ChatColor.translateAlternateColorCodes('&', reason);
        Bukkit.getPlayer(uuid).sendMessage(JanetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "You were warned for " + reason + ".");
        broadcast(JanetName + getName(uuid) + " was warned by " + warner + " for " + reason + ".", uuid);
        return getName(uuid) + " was warned by " + warner + " for " + reason + ".";
    }

    private String other(UUID uuid, String reason) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(JanetName + pname + " was kicked for " + reason + ".");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "You were kicked for " + reason);
        return pname + " was kicked for " + reason + ".";
    }

    private String chatSpam(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(JanetName + pname + " was kicked for spamming the chat.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Don't spam the chat!");
        return pname + " was kicked for spamming the chat.";
    }

    private String cmdSpam(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(JanetName + pname + " was kicked for spamming commands.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Don't spam commands!");
        return pname + " was kicked for spamming commands.";
    }

    private String language(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(JanetName + pname + " was kicked for using bad language.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Watch your language!");
        return pname + " was kicked for using bad language.";
    }

    private String advertising(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(JanetName + pname + " was kicked for advertising.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Do not advertise other servers!");
        return pname + " was kicked for advertising.";
    }

    private String getName(UUID uuid) {
        return Bukkit.getPlayer(uuid).getName();
    }

    private void broadcast(String message, UUID uuid) {
        Bukkit.getConsoleSender().sendMessage(message);
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getUniqueId() != uuid)
                p.sendMessage(message);
    }
}