package com.crossge.necessities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GetUUID {
    private static HashMap<String, UUID> uuids = new HashMap<>();
    private File configFileUUIDs = new File("plugins/Necessities/RankManager", "users.yml");

    public UUID getID(String name) {
        UUID partial = null;
        boolean startsWith = false;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name))
                return p.getUniqueId();
            if (!startsWith && p.getName().toLowerCase().startsWith(name.toLowerCase())) {
                partial = p.getUniqueId();
                startsWith = true;
            }
            if (partial == null && (p.getName().toLowerCase().contains(name.toLowerCase()) || ChatColor.stripColor(p.getDisplayName()).toLowerCase().contains(name.toLowerCase())))
                partial = p.getUniqueId();
        }
        return partial;
    }

    public void addUUID(UUID uuid) {
        uuids.put(Bukkit.getPlayer(uuid).getName().toLowerCase(), uuid);
    }

    public UUID getOfflineID(String name) {
        if (uuids.containsKey(name.toLowerCase()))
            return uuids.get(name.toLowerCase());
        return null;
    }

    public void initiate() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Retrieving all stored UUIDs.");
        YamlConfiguration configUUIDs = YamlConfiguration.loadConfiguration(configFileUUIDs);
        ArrayList<String> invalidKeys = new ArrayList<>();
        for (String key : configUUIDs.getKeys(false))
            if (nameFromString(key) != null) {
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(key);
                } catch (Exception e) {
                    invalidKeys.add(key);
                }
                if (uuid != null)
                    uuids.put(nameFromString(key).toLowerCase(), UUID.fromString(key));
            } else
                invalidKeys.add(key);
        if (!invalidKeys.isEmpty() && invalidKeys.size() < 3) {
            Bukkit.broadcast("Invalid keys found.", "Necessities.opBroadcast");
            for (String key : invalidKeys)
                //configUUIDs.set(key, null);
                Bukkit.broadcast("Invalid key: " + key, "Necessities.opBroadcast");
            /*try {
                configUUIDs.save(configFileUUIDs);
            } catch (Exception e) {
            }*/
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "All stored UUIDs retrieved.");
    }

    public boolean hasJoined(UUID uuid) {
        return uuids.containsValue(uuid);
    }

    public String nameFromString(String message) {
        UUID uuid;
        try {
            uuid = UUID.fromString(message);
        } catch (Exception e) {
            return null;
        }
        if (Bukkit.getPlayer(uuid) == null) {
            if (Bukkit.getOfflinePlayer(uuid) == null)
                return null; //What did you do this time
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
        return Bukkit.getPlayer(uuid).getName();
    }
}