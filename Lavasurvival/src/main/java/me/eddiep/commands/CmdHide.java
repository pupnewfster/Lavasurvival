package me.eddiep.commands;

import me.eddiep.ranks.UserInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class CmdHide extends Cmd {
    private static ArrayList<UUID> hidden = new ArrayList<UUID>();

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            UserInfo u = um.getUser(p.getUniqueId());
            if (hidden.contains(p.getUniqueId())) {
                unhidePlayer(p);
                Bukkit.broadcastMessage("");
                hidden.remove(p.getUniqueId());
                p.sendMessage(ChatColor.GOLD + "You are now visible.");
                Bukkit.broadcast(ChatColor.GOLD + "To Ops - " + ChatColor.RED + p.getDisplayName() + ChatColor.GOLD + " - is now " + ChatColor.DARK_GRAY +
                        "visible" + ChatColor.GOLD + ".", "lavasurvival.opchat");
                if (u.isInOpChat()) {
                    u.toggleOpChat();
                    p.sendMessage(ChatColor.GOLD + "You are no longer sending messages to ops.");
                }
            } else {
                hidePlayer(p);
                Bukkit.broadcastMessage("");
                hidden.add(p.getUniqueId());
                p.sendMessage(ChatColor.GOLD + "You are now hidden.");
                Bukkit.broadcast(ChatColor.GOLD + "To Ops - " + ChatColor.RED + p.getDisplayName() + ChatColor.GOLD + " - is now " + ChatColor.WHITE +
                        "invisible" + ChatColor.GOLD + ".", "lavasurvival.opchat");
                if (!u.isInOpChat()) {
                    u.toggleOpChat();
                    p.sendMessage(ChatColor.GOLD + "You are now sending messages only to ops.");
                }
            }
        } else
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "The console cannot hide.");
        return true;
    }

    public void removeP(UUID uuid) {
        if (hidden.contains(uuid)) {
            hidden.remove(uuid);
            unhidePlayer(Bukkit.getPlayer(uuid));
        }
    }

    public boolean isHidden(Player p) {
        return hidden.contains(p.getUniqueId());
    }

    public void playerJoined(Player p) {
        if (!p.hasPermission("lavasurvival.hide"))
            for (UUID uuid : hidden)
                p.hidePlayer(Bukkit.getPlayer(uuid));
    }

    public void playerLeft(Player p) {
        for (UUID uuid : hidden)
            p.showPlayer(Bukkit.getPlayer(uuid));
    }

    private void hidePlayer(Player p) {
        for (Player x : Bukkit.getOnlinePlayers())
            if (!x.equals(p) && x.canSee(p) && !x.hasPermission("lavasurvival.hide"))
                x.hidePlayer(p);
    }

    private void unhidePlayer(Player p) {
        for (Player x : Bukkit.getOnlinePlayers())
            if (!x.equals(p) && !x.canSee(p))
                x.showPlayer(p);
    }
}