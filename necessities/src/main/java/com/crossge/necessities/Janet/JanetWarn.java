package com.crossge.necessities.Janet;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class JanetWarn {
    private final HashMap<UUID, Integer> warnCount = new HashMap<>();
    private String janetName = "";
    private int warns;

    public void initiate() {
        RankManager rm = Necessities.getInstance().getRM();
        this.janetName = (!rm.getOrder().isEmpty() ? ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ") : "") + "Janet" + ChatColor.DARK_RED + ": " + ChatColor.WHITE;
        this.warns = Necessities.getInstance().getConfig().getInt("Necessities.warns");
    }

    void removePlayer(UUID uuid) {
        this.warnCount.remove(uuid);
    }

    public void warn(UUID uuid, String reason, String warner) {
        if (!this.warnCount.containsKey(uuid))
            this.warnCount.put(uuid, 1);
        else
            this.warnCount.put(uuid, this.warnCount.get(uuid) + 1);
        String warning;
        if (this.warnCount.get(uuid) == this.warns) {
            switch (reason) {
                case "Language":
                    warning = language(uuid);
                    break;
                case "ChatSpam":
                    warning = chatSpam(uuid);
                    break;
                case "CmdSpam":
                    warning = cmdSpam(uuid);
                    break;
                case "Adds":
                    warning = advertising(uuid);
                    break;
                default:
                    warning = other(uuid, reason);
                    break;
            }
        } else {
            switch (reason) {
                case "Language":
                    warning = langMsg(uuid);
                    break;
                case "ChatSpam":
                    warning = chatMsg(uuid);
                    break;
                case "CmdSpam":
                    warning = cmdMsg(uuid);
                    break;
                case "Adds":
                    warning = addsMsg(uuid);
                    break;
                default:
                    warning = warnMessage(uuid, reason, warner);
                    break;
            }
            timesLeft(uuid);
        }
        Necessities.getInstance().getLog().log("Janet: " + warning);
    }

    private void timesLeft(UUID uuid) {
        String left = Integer.toString(this.warns - this.warnCount.get(uuid));
        String plural = "times";
        if (this.warns - this.warnCount.get(uuid) == 1)
            plural = "time";
        Bukkit.getPlayer(uuid).sendMessage(this.janetName + "Do it " + left + " more " + plural + " and you will be kicked.");
    }

    private String langMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(this.janetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not swear please.");
        broadcast(this.janetName + getName(uuid) + " was warned for using bad language.", uuid);
        return getName(uuid) + " was warned for using bad language.";
    }

    private String chatMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(this.janetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not spam the chat please.");
        broadcast(this.janetName + getName(uuid) + " was warned for spamming the chat.", uuid);
        return getName(uuid) + " was warned for spamming the chat.";
    }

    private String cmdMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(this.janetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not spam commands please.");
        broadcast(this.janetName + getName(uuid) + " was warned for spamming commands.", uuid);
        return getName(uuid) + " was warned for spamming commands.";
    }

    private String addsMsg(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(this.janetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "Do not advertise other servers please.");
        broadcast(this.janetName + getName(uuid) + " was warned for advertising other servers.", uuid);
        return getName(uuid) + " was warned for advertising other servers.";
    }

    private String warnMessage(UUID uuid, String reason, String warner) {
        reason = ChatColor.translateAlternateColorCodes('&', reason);
        Bukkit.getPlayer(uuid).sendMessage(this.janetName + ChatColor.DARK_RED + "Warning, " + ChatColor.WHITE + "You were warned for " + reason + ".");
        broadcast(this.janetName + getName(uuid) + " was warned by " + warner + " for " + reason + ".", uuid);
        return getName(uuid) + " was warned by " + warner + " for " + reason + ".";
    }

    private String other(UUID uuid, String reason) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(this.janetName + pname + " was kicked for " + reason + ".");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "You were kicked for " + reason);
        return pname + " was kicked for " + reason + ".";
    }

    private String chatSpam(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(this.janetName + pname + " was kicked for spamming the chat.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Don't spam the chat!");
        return pname + " was kicked for spamming the chat.";
    }

    private String cmdSpam(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(this.janetName + pname + " was kicked for spamming commands.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Don't spam commands!");
        return pname + " was kicked for spamming commands.";
    }

    private String language(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(this.janetName + pname + " was kicked for using bad language.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Watch your language!");
        return pname + " was kicked for using bad language.";
    }

    private String advertising(UUID uuid) {
        String pname = getName(uuid);
        Bukkit.broadcastMessage(this.janetName + pname + " was kicked for advertising.");
        Bukkit.getPlayer(uuid).kickPlayer(ChatColor.WHITE + "Do not advertise other servers!");
        return pname + " was kicked for advertising.";
    }

    private String getName(UUID uuid) {
        return Bukkit.getPlayer(uuid).getName();
    }

    private void broadcast(String message, UUID uuid) {
        Bukkit.getConsoleSender().sendMessage(message);
        Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId() != uuid).forEach(p -> p.sendMessage(message));
    }
}