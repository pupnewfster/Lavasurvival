package me.eddiep.minecraft.ls.game.options;

import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldCanBeLocal", "SameParameterValue", "unused"})
public class FloodOptions extends BaseOptions {
    private transient Vector selectedSpawn;
    private transient LavaMap owner;

    private final List<Vector> spawnPoints = new ArrayList<>();
    private int minPrepareTimeSeconds = 300;
    private int maxPrepareTimeSeconds = 480;
    private int minEndTimeSeconds = 180;
    private int maxEndTimeSeconds = 420;

    public static FloodOptions defaults(LavaMap lavaMap) {
        return new FloodOptions(lavaMap);
    }

    FloodOptions(LavaMap owner) {
        this.owner = owner;
    }

    public long generateRandomPrepareTime() {
        return (RANDOM.nextInt(maxPrepareTimeSeconds - minPrepareTimeSeconds) + minPrepareTimeSeconds) * 1000L;
    }

    public long generateRandomEndTime() {
        int min = minEndTimeSeconds, max = maxEndTimeSeconds;
        if (Gamemode.getCurrentGame().aliveCount() <= 10) {
            min /= 2;
            max /= 2;
        } else if (Gamemode.getCurrentGame().aliveCount() <= 20) {
            min /= 1.5;
            max /= 1.5;
        }

        return (RANDOM.nextInt(max - min) + min) * 1000L;
    }

    @SuppressWarnings("deprecation")
    public List<Vector> getSpawnPoints() {
        if (!isUsingMultiSpawn())
            return Collections.singletonList(new Vector(this.owner.getLavaX(), this.owner.getLavaY(), this.owner.getLavaZ()));
        return this.spawnPoints;
    }

    @SuppressWarnings("deprecation")
    public List<Location> getSpawnLocations() {
        if (!isUsingMultiSpawn())
            return Collections.singletonList(this.owner.getLavaSpawnAsLocation());
        return this.spawnPoints.stream().map(vector -> new Location(this.owner.getWorld(), vector.getX(), vector.getY(), vector.getZ())).collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    private Vector getHighestSpawn() {
        if (!isUsingMultiSpawn())
            return this.owner.getLavaSpawnAsLocation().toVector();
        Vector highest = this.spawnPoints.get(0);
        for (Vector vector : this.spawnPoints)
            if (vector.getY() > highest.getY())
                highest = vector;
        return highest;
    }

    public Location getHighestLocation() {
        Vector highest = getHighestSpawn();
        return new Location(this.owner.getWorld(), highest.getX(), highest.getY(), highest.getZ());
    }

    @SuppressWarnings("deprecation")
    public List<Location> getSpawnLocation(int xoffset, int yoffset, int zoffet) {
        if (!isUsingMultiSpawn())
            return Collections.singletonList(this.owner.getLavaSpawnAsLocation(xoffset, yoffset, zoffet));
        return this.spawnPoints.stream().map(vector -> new Location(this.owner.getWorld(), vector.getX() + xoffset, vector.getY() + yoffset, vector.getZ() + zoffet)).collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    private Vector getSingleSpawn() {
        if (!isUsingMultiSpawn())
            return new Vector(this.owner.getLavaX(), this.owner.getLavaY(), this.owner.getLavaZ());
        else {
            if (this.selectedSpawn == null)
                this.selectedSpawn = this.spawnPoints.get(RANDOM.nextInt(this.spawnPoints.size()));
            return this.selectedSpawn;
        }
    }

    public Location getSingleSpawnLocation() {
        Vector spawn = getSingleSpawn();
        return new Location(this.owner.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "ConstantConditions"})
    private boolean isUsingMultiSpawn() {
        return this.spawnPoints != null && this.spawnPoints.size() > 0 && super.isEnabled();
    }

    public void setParent(LavaMap parent) {
        this.owner = parent;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasParent() {
        return this.owner != null;
    }
}