package me.eddiep.minecraft.ls.game.options;

import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloodOptions extends BaseOptions {
    private transient Vector selectedSpawn;
    private transient LavaMap owner;

    private List<Vector> spawnPoints = new ArrayList<>();
    private int minPrepareTimeSeconds = 300;
    private int maxPrepareTimeSeconds = 480;
    private int minEndTimeSeconds = 180;
    private int maxEndTimeSeconds = 420;
    private boolean enableLava = true;
    private boolean enableWater = true;

    public static FloodOptions defaults(LavaMap lavaMap) {
        return new FloodOptions(lavaMap);
    }

    FloodOptions(LavaMap owner) { this.owner = owner; }

    public long generateRandomPrepareTime() {
        int seconds = RANDOM.nextInt(maxPrepareTimeSeconds - minPrepareTimeSeconds) + minPrepareTimeSeconds;

        return seconds * 1000L;
    }

    public long generateRandomEndTime() {
        int seconds = RANDOM.nextInt(maxEndTimeSeconds - minEndTimeSeconds) + minEndTimeSeconds;

        return seconds * 1000L;
    }

    public boolean isLavaEnabled() {
        return enableLava;
    }

    public boolean isWaterEnabled() {
        return enableWater;
    }

    public List<Vector> getSpawnPoints() {
        if (!isUsingMultiSpawn()) {
            return Arrays.asList(new Vector(owner.getLavaX(), owner.getLavaY(), owner.getLavaZ()));
        }
        return spawnPoints;
    }

    public List<Location> getSpawnLocations() {
        if (!isUsingMultiSpawn()) {
            return Arrays.asList(owner.getLavaSpawnAsLocation());
        }

        List<Location> list = new ArrayList<>();
        for (Vector vector : spawnPoints) {
            Location location = new Location(owner.getWorld(), vector.getX(), vector.getY(), vector.getZ());
            list.add(location);
        }

        return list;
    }

    public Vector getHighestSpawn() {
        if (!isUsingMultiSpawn()) {
            return owner.getLavaSpawnAsLocation().toVector();
        }

        Vector highest = spawnPoints.get(0);
        for (Vector vector : spawnPoints) {
            if (vector.getY() > highest.getY())
                highest = vector;
        }

        return highest;
    }

    public Location getHighestLocation() {
        Vector highest = getHighestSpawn();
        return new Location(owner.getWorld(), highest.getX(), highest.getY(), highest.getZ());
    }

    public List<Location> getSpawnLocation(int xoffset, int yoffset, int zoffet) {
        if (!isUsingMultiSpawn()) {
            return Arrays.asList(owner.getLavaSpawnAsLocation(xoffset, yoffset, zoffet));
        }

        List<Location> list = new ArrayList<>();
        for (Vector vector : spawnPoints) {
            Location location = new Location(owner.getWorld(), vector.getX() + xoffset, vector.getY() + yoffset, vector.getZ() + zoffet);
            list.add(location);
        }

        return list;
    }

    public Vector getSingleSpawn() {
        if (!isUsingMultiSpawn()) {
            return new Vector(owner.getLavaX(), owner.getLavaY(), owner.getLavaZ());
        } else {
            if (selectedSpawn == null) {
                selectedSpawn = spawnPoints.get(RANDOM.nextInt(spawnPoints.size()));
            }
            return selectedSpawn;
        }
    }

    public Location getSingleSpawnLocation() {
        Vector spawn = getSingleSpawn();
        return new Location(owner.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
    }

    public boolean isUsingMultiSpawn() {
        return spawnPoints != null && spawnPoints.size() > 0 && super.isEnabled();
    }

    public void setParent(LavaMap parent) {
        this.owner = parent;
    }

    public boolean hasParent() {
        return this.owner != null;
    }
}
