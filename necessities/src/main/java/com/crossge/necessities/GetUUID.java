package com.crossge.necessities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.io.BufferedReader;
import java.net.URL;
import java.util.UUID;

public class GetUUID {
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

    public UUID getOfflineID(String name) {
        if (name == null)
            return null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection().getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            return UUID.fromString(((String) Jsoner.deserialize(response.toString(), new JsonObject()).get("id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Exception ignored) {
        }
        return null;
    }

    public String nameFromString(String message) {
        if (message == null)
            return null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/user/profiles/" + message.replaceAll("-", "") + "/names").openConnection().getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JsonArray json = Jsoner.deserialize(response.toString(), new JsonArray());
            return ((JsonObject) json.get(json.size() - 1)).getString("name");
        } catch (Exception ignored) {
        }
        return null;
    }
}