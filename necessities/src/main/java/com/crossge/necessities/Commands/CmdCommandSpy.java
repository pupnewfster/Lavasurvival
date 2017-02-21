package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdCommandSpy implements Cmd {
    private final ArrayList<UUID> spying = new ArrayList<>();

    public void broadcast(String sender, String command) {
        ArrayList<UUID> temp = new ArrayList<>();
        this.spying.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).forEach(uuid -> {
            if (Bukkit.getPlayer(uuid).hasPermission("Necessities.spy"))
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.AQUA + sender + ": " + command);
            else
                temp.add(uuid);
        });
        this.spying.removeAll(temp);
    }

    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getVar();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(var.getMessages() + (this.spying.contains(p.getUniqueId()) ? "No longer" : "You are now") + " spying on commands.");
            if (this.spying.contains(p.getUniqueId()))
                this.spying.remove(p.getUniqueId());
            else
                this.spying.add(p.getUniqueId());
        } else
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "The console can already see all commands.");
        return true;
    }

    public void unload() {
        File configFileSpying = new File(Necessities.getInstance().getDataFolder(), "spying.yml");
        YamlConfiguration configSpying = YamlConfiguration.loadConfiguration(configFileSpying);
        configSpying.getKeys(false).forEach(key -> configSpying.set(key, null));
        this.spying.forEach(uuid -> configSpying.set(uuid.toString(), true));
        try {
            configSpying.save(configFileSpying);
        } catch (Exception ignored) {
        }
    }

    public void init() {
        YamlConfiguration configSpying = YamlConfiguration.loadConfiguration(new File(Necessities.getInstance().getDataFolder(), "spying.yml"));
        this.spying.addAll(configSpying.getKeys(false).stream().map(UUID::fromString).collect(Collectors.toList()));
    }
}