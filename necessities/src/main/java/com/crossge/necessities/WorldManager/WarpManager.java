package com.crossge.necessities.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class WarpManager {
    private final HashMap<String, Warp> warps = new HashMap<>();
    private final HashMap<String, String> lowerNames = new HashMap<>();
    private final File configFileWarps = new File("plugins/Necessities/WorldManager", "warps.yml");

    public void initiate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loading warps...");
        YamlConfiguration configWarps = YamlConfiguration.loadConfiguration(this.configFileWarps);
        for (String warp : configWarps.getKeys(false)) {
            this.warps.put(warp, new Warp(warp));
            this.lowerNames.put(warp.toLowerCase(), warp);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All warps loaded.");
    }

    public boolean isWarp(String name) {
        return this.lowerNames.containsKey(name.toLowerCase());
    }

    public Warp getWarp(String name) {
        return !isWarp(name) ? null : this.warps.get(this.lowerNames.get(name.toLowerCase()));
    }

    public String getWarps() {
        ArrayList<String> ws = new ArrayList<>();
        ws.addAll(this.warps.keySet());
        Collections.sort(ws);
        StringBuilder warpsBuilder = new StringBuilder();
        for (String w : ws)
            warpsBuilder.append(w).append(", ");
        String warps = warpsBuilder.toString();
        return warps.equals("") ? "" : warps.trim().substring(0, warps.length() - 2);
    }

    public void remove(String name) {
        YamlConfiguration configWarps = YamlConfiguration.loadConfiguration(this.configFileWarps);
        configWarps.set(name, null);
        try {
            configWarps.save(this.configFileWarps);
        } catch (Exception ignored) {
        }
        this.warps.remove(name);
        this.lowerNames.remove(name.toLowerCase());
    }

    public void create(String name, Location loc) {
        YamlConfiguration configWarps = YamlConfiguration.loadConfiguration(this.configFileWarps);
        configWarps.set(name + ".world", loc.getWorld().getName());
        configWarps.set(name + ".x", loc.getX());
        configWarps.set(name + ".y", loc.getY());
        configWarps.set(name + ".z", loc.getZ());
        configWarps.set(name + ".yaw", loc.getYaw());
        configWarps.set(name + ".pitch", loc.getPitch());
        try {
            configWarps.save(this.configFileWarps);
        } catch (Exception ignored) {
        }
        this.warps.put(name, new Warp(name));
        this.lowerNames.put(name.toLowerCase(), name);
    }
}