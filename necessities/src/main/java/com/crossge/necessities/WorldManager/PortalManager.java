package com.crossge.necessities.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class PortalManager {//TODO: add a method to update the things when a world is unloaded or loaded
    private static final HashMap<String, Portal> portals = new HashMap<>();
    private static final HashMap<String, String> lowerNames = new HashMap<>();
    private final File configFilePM = new File("plugins/Necessities/WorldManager", "portals.yml");

    public void initiate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loading portals...");
        YamlConfiguration configPM = YamlConfiguration.loadConfiguration(this.configFilePM);
        for (String portal : configPM.getKeys(false)) {
            portals.put(portal, new Portal(portal));
            lowerNames.put(portal.toLowerCase(), portal);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All portals loaded.");
    }

    public Location portalDestination(Location l) {
        for (String key : portals.keySet())
            if (portals.get(key).isPortal(l)) {
                if (portals.get(key).isWarp())
                    return portals.get(key).getWarp().getDestination();
                return portals.get(key).getWorldTo().getSpawnLocation();
            }
        return null;
    }

    public boolean exists(String name) {
        return lowerNames.containsKey(name.toLowerCase());
    }

    public void remove(String name) {
        YamlConfiguration configPM = YamlConfiguration.loadConfiguration(this.configFilePM);
        configPM.set(name, null);
        try {
            configPM.save(this.configFilePM);
        } catch (Exception ignored) {
        }
        portals.remove(name);
        lowerNames.remove(name.toLowerCase());
    }

    public void create(String name, String destination, Location left, Location right) {
        YamlConfiguration configPM = YamlConfiguration.loadConfiguration(this.configFilePM);
        configPM.set(name + ".world", left.getWorld().getName());
        configPM.set(name + ".destination", destination);
        configPM.set(name + ".location.x1", left.getBlockX());
        configPM.set(name + ".location.y1", left.getBlockY());
        configPM.set(name + ".location.z1", left.getBlockZ());
        configPM.set(name + ".location.x2", right.getBlockX());
        configPM.set(name + ".location.y2", right.getBlockY());
        configPM.set(name + ".location.z2", right.getBlockZ());
        try {
            configPM.save(this.configFilePM);
        } catch (Exception ignored) {
        }
        portals.put(name, new Portal(name));
        lowerNames.put(name.toLowerCase(), name);
    }
} 