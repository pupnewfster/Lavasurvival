package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class CmdHide extends Cmd {
    private static ArrayList<UUID> hidden = new ArrayList<UUID>();
    private File configFileHiding = new File("plugins/Necessities", "hiding.yml");

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = um.getUser(p.getUniqueId());
            if (hidden.contains(p.getUniqueId())) {
                unhidePlayer(p);
                hidden.remove(p.getUniqueId());
                p.sendMessage(var.getMessages() + "You are now visible.");
                Bukkit.broadcast(var.getMessages() + "To Ops - " + var.getObj() + p.getDisplayName() + var.getMessages() + " - is now " + ChatColor.DARK_GRAY +
                        "visible" + var.getMessages() + ".", "Necessities.opBroadcast");
                RankManager rm = new RankManager();
                String rank = "";
                if (!rm.getOrder().isEmpty())
                    rank = ChatColor.translateAlternateColorCodes('&', rm.getRank(rm.getOrder().size() - 1).getTitle() + " ");
                Bukkit.broadcastMessage(rank + "Janet" + ChatColor.DARK_RED + ": " + ChatColor.WHITE + "Welcome Back.");
                if (u.opChat()) {
                    u.toggleOpChat();
                    p.sendMessage(var.getMessages() + "You are no longer sending messages to ops.");
                }
            } else {
                hidePlayer(p);
                hidden.add(p.getUniqueId());
                p.sendMessage(var.getMessages() + "You are now hidden.");
                Bukkit.broadcast(var.getMessages() + "To Ops - " + var.getObj() + p.getDisplayName() + var.getMessages() + " - is now " + ChatColor.WHITE +
                        "invisible" + var.getMessages() + ".", "Necessities.opBroadcast");
                if (!u.opChat()) {
                    u.toggleOpChat();
                    p.sendMessage(var.getMessages() + "You are now sending messages only to ops.");
                }
            }
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console cannot hide.");
        return true;
    }

    public boolean isHidden(Player p) {
        return p != null && hidden.contains(p.getUniqueId());
    }

    public void playerJoined(Player p) {
        if (!p.hasPermission("Necessities.seehidden"))
            for (UUID uuid : hidden)
            if (Bukkit.getPlayer(uuid) != null)
                p.hidePlayer(Bukkit.getPlayer(uuid));
    }

    public void playerLeft(Player p) {
        for (UUID uuid : hidden)
            if (Bukkit.getPlayer(uuid) != null)
                p.showPlayer(Bukkit.getPlayer(uuid));
    }

    public void hidePlayer(Player p) {
        for (Player x : Bukkit.getOnlinePlayers())
            if (!x.equals(p) && x.canSee(p) && !x.hasPermission("Necessities.seehidden"))
                x.hidePlayer(p);
        Necessities.getInstance().removePlayer(p);
    }

    private void unhidePlayer(Player p) {
        for (Player x : Bukkit.getOnlinePlayers())
            if (!x.equals(p) && !x.canSee(p))
                x.showPlayer(p);
        Necessities.getInstance().addPlayer(p);
    }

    public void unload() {
        YamlConfiguration configHiding = YamlConfiguration.loadConfiguration(configFileHiding);
        for (String key : configHiding.getKeys(false))
            configHiding.set(key, null);
        for (UUID uuid : hidden)
            configHiding.set(uuid.toString(), true);
        try {
            configHiding.save(configFileHiding);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        YamlConfiguration configSpying = YamlConfiguration.loadConfiguration(configFileHiding);
        for (String key : configSpying.getKeys(false))
            hidden.add(UUID.fromString(key));
    }
}