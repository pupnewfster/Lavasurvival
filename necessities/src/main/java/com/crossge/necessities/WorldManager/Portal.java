package com.crossge.necessities.WorldManager;

import com.crossge.necessities.Necessities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

class Portal {
    private double x1, y1, z1, x2, y2, z2;
    private boolean validPortal;
    private Warp destination;
    private World from, to;
    private final String name;

    Portal(String portalName) {
        File configFilePM = new File("plugins/Necessities/WorldManager", "portals.yml");
        YamlConfiguration configPM = YamlConfiguration.loadConfiguration(configFilePM);
        WarpManager warps = Necessities.getWarps();
        this.name = portalName;
        if (configPM.contains(this.name + ".world"))
            this.from = Bukkit.getWorld(configPM.getString(this.name + ".world"));
        if (configPM.contains(this.name + ".destination")) {
            String dest = configPM.getString(this.name + ".destination");
            if (dest.startsWith("-"))
                this.destination = warps.getWarp(dest.replaceFirst("-", ""));
            else
                this.to = Bukkit.getWorld(dest);
        }
        if (configPM.contains(this.name + ".location.x1"))
            this.x1 = configPM.getDouble(this.name + ".location.x1");
        if (configPM.contains(this.name + ".location.y1"))
            this.y1 = configPM.getDouble(this.name + ".location.y1");
        if (configPM.contains(this.name + ".location.z1"))
            this.z1 = configPM.getDouble(this.name + ".location.z1");
        if (configPM.contains(this.name + ".location.x2"))
            this.x2 = configPM.getDouble(this.name + ".location.x2");
        if (configPM.contains(this.name + ".location.y2"))
            this.y2 = configPM.getDouble(this.name + ".location.y2");
        if (configPM.contains(this.name + ".location.z2"))
            this.z2 = configPM.getDouble(this.name + ".location.z2");
        if (this.from != null && (this.to != null || this.destination != null)) {
            this.validPortal = true;
            fixCoordinates();
        }
    }

    private void fixCoordinates() {
        if (this.x1 > this.x2) {
            double tempX = this.x1;
            this.x1 = this.x2;
            this.x2 = tempX;
        }
        if (this.y1 > this.y2) {
            double tempY = this.y1;
            this.y1 = this.y2;
            this.y2 = tempY;
        }
        if (this.z1 > this.z2) {
            double tempZ = this.z1;
            this.z1 = this.z2;
            this.z2 = tempZ;
        }
        this.x1 -= 0.3;
        this.y1 -= 0.3;
        this.z1 -= 0.3;
        this.x2 += 0.3;
        this.y2 += 0.3;
        this.z2 += 0.3;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return this.name;
    }

    World getWorldTo() {
        return this.to;
    }

    Warp getWarp() {
        return this.destination;
    }

    boolean isWarp() {
        return (this.destination != null && this.destination.hasDestination());
    }

    boolean isPortal(Location l) {
        if ((this.to == null && !isWarp()) || this.from == null)
            return false;
        WorldManager wm = Necessities.getWM();
        return this.validPortal && this.from.equals(l.getWorld()) && (isWarp() || wm.worldExists(this.to.getName())) && this.x1 <= l.getBlockX() && l.getBlockX() <= this.x2 && this.y1 <= l.getBlockY() &&
                l.getBlockY() <= this.y2 && this.z1 <= l.getBlockZ() && l.getBlockZ() <= this.z2;
    }
}