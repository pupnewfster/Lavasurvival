package com.crossge.necessities.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class CmdCommandSpy extends Cmd {
    private static ArrayList<UUID> spying = new ArrayList<UUID>();
    private File configFileSpying = new File("plugins/Necessities", "spying.yml");

    public void broadcast(String sender, String command) {
        ArrayList<UUID> temp = new ArrayList<UUID>();
        for (UUID uuid : spying)
            if (Bukkit.getPlayer(uuid) != null) {
                if (Bukkit.getPlayer(uuid).hasPermission("Necessities.spy"))
                    Bukkit.getPlayer(uuid).sendMessage(ChatColor.AQUA + sender + ": " + command);
                else
                    temp.add(uuid);
            }
        for (UUID uuid : temp)
            spying.remove(uuid);
    }

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (spying.contains(p.getUniqueId())) {
                p.sendMessage(var.getMessages() + "No longer spying on commands.");
                spying.remove(p.getUniqueId());
            } else {
                p.sendMessage(var.getMessages() + "You are now spying on commands.");
                spying.add(p.getUniqueId());
            }
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console can already see all commands.");
        return true;
    }

    public void unload() {
        YamlConfiguration configSpying = YamlConfiguration.loadConfiguration(configFileSpying);
        for (String key : configSpying.getKeys(false))
            configSpying.set(key, null);
        for (UUID uuid : spying)
            configSpying.set(uuid.toString(), true);
        try {
            configSpying.save(configFileSpying);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        YamlConfiguration configSpying = YamlConfiguration.loadConfiguration(configFileSpying);
        for (String key : configSpying.getKeys(false))
            spying.add(UUID.fromString(key));
    }
}