package com.crossge.necessities;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class Formatter {
    public boolean isLegal(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public String addCommas(int i) {
        return new DecimalFormat("#,###").format(i);
    }

    public String capFirst(String matName) {
        if (matName == null)
            return "";
        String name = "";
        matName = matName.replaceAll("_", " ").toLowerCase();
        String[] namePieces = matName.split(" ");
        for (String piece : namePieces)
            name += upercaseFirst(piece) + " ";
        return name.trim();
    }

    private String upercaseFirst(String word) {
        if (word == null)
            return "";
        String firstCapitalized = "";
        if (word.length() > 0)
            firstCapitalized = word.substring(0, 1).toUpperCase();
        if (word.length() > 1)
            firstCapitalized += word.substring(1);
        return firstCapitalized;
    }

    public String ownerShip(String name) {
        return (name.endsWith("s") || name.endsWith("S")) ? name + "'" : name + "'s";
    }

    public String getTPS() {
        String ticks = ChatColor.GOLD + "TPS from last 1m, 5m, 15m: " ;
        for (double tps : getNMSRecentTps())
            ticks += format(tps) + ", ";
        return ticks.substring(0, ticks.length() - 2).trim();
    }

    private String format(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    private static final Field recentTpsField = makeField(MinecraftServer.class, "recentTps");
    private double[] getNMSRecentTps() {
        if (recentTpsField == null)
            return new double[0];
        return getField(recentTpsField, MinecraftServer.getServer());
    }

    private static Field makeField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Field field, Object instance) {
        if (field == null) throw new RuntimeException("No such field");
        field.setAccessible(true);
        try {
            return (T) field.get(instance);
        } catch (Exception ex) {
            return null;
        }
    }
}