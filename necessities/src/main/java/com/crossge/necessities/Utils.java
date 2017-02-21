package com.crossge.necessities;

import net.minecraft.server.v1_11_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.entity.Player;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.UUID;

public class Utils {
    @SuppressWarnings({"ResultOfMethodCallIgnored", "BooleanMethodIsAlwaysInverted"})
    public static boolean legalDouble(String input) {
        try {
            Double.parseDouble(input);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean legalInt(String input) {
        try {
            Integer.parseInt(input);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String addCommas(int i) {
        return new DecimalFormat("#,###").format(i);
    }

    public static String formatMoney(double d) {
        return new DecimalFormat("#,##0.00").format(d);
    }

    public static String capFirst(String matName) {
        if (matName == null)
            return "";
        StringBuilder name = new StringBuilder();
        matName = matName.replaceAll("_", " ").toLowerCase();
        String[] namePieces = matName.split(" ");
        for (String piece : namePieces)
            name.append(uppercaseFirst(piece)).append(" ");
        return name.toString().trim();
    }

    private static String uppercaseFirst(String word) {
        if (word == null)
            return "";
        String firstCapitalized = "";
        if (word.length() > 0)
            firstCapitalized = word.substring(0, 1).toUpperCase();
        if (word.length() > 1)
            firstCapitalized += word.substring(1);
        return firstCapitalized;
    }

    public static String ownerShip(String name) {
        return (name.endsWith("s") || name.endsWith("S")) ? name + "'" : name + "'s";
    }

    public static UUID getID(String name) {
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

    public static UUID getOfflineID(String name) {
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

    public static String nameFromString(String message) {
        if (message == null)
            return null;
        try {
            UUID uuid = UUID.fromString(message);
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            if (p.hasPlayedBefore()) //If we have a player data for that uuid get their name  sort of as a cache
                return p.getName();
        } catch (Exception ignored) {
        }
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

    public static String getTPS() {
        StringBuilder ticksBuilder = new StringBuilder(ChatColor.GOLD + "TPS from last 1m, 5m, 15m: ");
        for (double tps : getNMSRecentTps())
            ticksBuilder.append(format(tps)).append(", ");
        String ticks = ticksBuilder.toString();
        return ticks.substring(0, ticks.length() - 2).trim();
    }

    private static String format(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    private static final Field recentTpsField = makeField(MinecraftServer.class);

    private static double[] getNMSRecentTps() {
        if (recentTpsField == null)
            return new double[0];
        return getField(((CraftServer) Bukkit.getServer()).getServer());
    }

    private static Field makeField(Class<?> clazz) {
        try {
            return clazz.getDeclaredField("recentTps");
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object instance) {
        if (Utils.recentTpsField == null) throw new RuntimeException("No such field");
        Utils.recentTpsField.setAccessible(true);
        try {
            return (T) Utils.recentTpsField.get(instance);
        } catch (Exception ex) {
            return null;
        }
    }
}