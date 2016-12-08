package com.crossge.necessities;

import net.minecraft.server.v1_11_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

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

    public static String capFirst(String matName) {
        if (matName == null)
            return "";
        String name = "";
        matName = matName.replaceAll("_", " ").toLowerCase();
        String[] namePieces = matName.split(" ");
        for (String piece : namePieces)
            name += uppercaseFirst(piece) + " ";
        return name.trim();
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

    public static String getTPS() {
        String ticks = ChatColor.GOLD + "TPS from last 1m, 5m, 15m: ";
        for (double tps : getNMSRecentTps())
            ticks += format(tps) + ", ";
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