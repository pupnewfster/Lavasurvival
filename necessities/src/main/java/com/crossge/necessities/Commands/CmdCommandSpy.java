package com.crossge.necessities.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class CmdCommandSpy implements Cmd {
    private static ArrayList<UUID> spying = new ArrayList<>();
    private File configFileSpying = new File("plugins/Necessities", "spying.yml");

    public void broadcast(String sender, String command) {
        ArrayList<UUID> temp = new ArrayList<>();
        spying.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).forEach(uuid -> {
            if (Bukkit.getPlayer(uuid).hasPermission("Necessities.spy"))
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.AQUA + sender + ": " + command);
            else
                temp.add(uuid);
        });
        for (UUID uuid : temp)
            spying.remove(uuid);
    }

    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(var.getMessages() + (spying.contains(p.getUniqueId()) ? "No longer" : "You are now") + " spying on commands.");
            if (spying.contains(p.getUniqueId()))
                spying.remove(p.getUniqueId());
            else
                spying.add(p.getUniqueId());
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
        } catch (Exception ignored) {
        }
    }

    public void init() {
        YamlConfiguration configSpying = YamlConfiguration.loadConfiguration(configFileSpying);
        for (String key : configSpying.getKeys(false))
            spying.add(UUID.fromString(key));
    }
}