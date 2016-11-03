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
import java.util.stream.Collectors;

public class CmdHide implements Cmd {
    private static ArrayList<UUID> hidden = new ArrayList<>();
    private File configFileHiding = new File("plugins/Necessities", "hiding.yml");
    private File configFileLogOut = new File("plugins/Necessities", "logoutmessages.yml");
    private File configFileLogIn = new File("plugins/Necessities", "loginmessages.yml");

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            User u = um.getUser(p.getUniqueId());
            if (hidden.contains(p.getUniqueId())) {
                unhidePlayer(p);
                YamlConfiguration configLogIn = YamlConfiguration.loadConfiguration(configFileLogIn);
                Bukkit.broadcastMessage((ChatColor.GREEN + " + " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                        configLogIn.getString(p.getUniqueId().toString()).replaceAll("\\{NAME\\}", p.getDisplayName()).replaceAll("\\{RANK\\}",
                                um.getUser(p.getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
                hidden.remove(p.getUniqueId());
                p.sendMessage(var.getMessages() + "You are now visible.");
                Bukkit.broadcast(var.getMessages() + "To Ops - " + var.getObj() + p.getDisplayName() + var.getMessages() + " - is now " + ChatColor.DARK_GRAY + "visible" + var.getMessages() + ".",
                        "Necessities.opBroadcast");
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
                YamlConfiguration configLogOut = YamlConfiguration.loadConfiguration(configFileLogOut);
                Bukkit.broadcastMessage((ChatColor.RED + " - " + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                        configLogOut.getString(p.getUniqueId().toString()).replaceAll("\\{NAME\\}", p.getDisplayName()).replaceAll("\\{RANK\\}",
                                um.getUser(p.getUniqueId()).getRank().getTitle()))).replaceAll(ChatColor.RESET + "", ChatColor.YELLOW + ""));
                hidden.add(p.getUniqueId());
                p.sendMessage(var.getMessages() + "You are now hidden.");
                Bukkit.broadcast(var.getMessages() + "To Ops - " + var.getObj() + p.getDisplayName() + var.getMessages() + " - is now " + ChatColor.WHITE + "invisible" + var.getMessages() + ".",
                        "Necessities.opBroadcast");
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
            hidden.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).forEach(uuid -> p.hidePlayer(Bukkit.getPlayer(uuid)));
    }

    public void playerLeft(Player p) {
        hidden.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).forEach(uuid -> p.showPlayer(Bukkit.getPlayer(uuid)));
    }

    public void hidePlayer(Player p) {
        Bukkit.getOnlinePlayers().stream().filter(x -> !x.equals(p) && x.canSee(p) && !x.hasPermission("Necessities.seehidden")).forEach(x -> x.hidePlayer(p));
        Necessities.getInstance().removePlayer(p);
    }

    private void unhidePlayer(Player p) {
        Bukkit.getOnlinePlayers().stream().filter(x -> !x.equals(p) && !x.canSee(p)).forEach(x -> x.showPlayer(p));
        Necessities.getInstance().addPlayer(p);
    }

    public void unload() {
        YamlConfiguration configHiding = YamlConfiguration.loadConfiguration(configFileHiding);
        configHiding.getKeys(false).forEach(key -> configHiding.set(key, null));
        hidden.forEach(uuid -> configHiding.set(uuid.toString(), true));
        try {
            configHiding.save(configFileHiding);
        } catch (Exception ignored) {
        }
    }

    public void init() {
        YamlConfiguration configSpying = YamlConfiguration.loadConfiguration(configFileHiding);
        hidden.addAll(configSpying.getKeys(false).stream().map(UUID::fromString).collect(Collectors.toList()));
    }
}