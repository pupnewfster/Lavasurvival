package me.eddiep.minecraft.ls.game.options;

import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LavaOptions extends BaseOptions {
    private transient Vector selectedSpawn;
    private transient LavaMap owner;

    private List<Vector> spawnPoints = new ArrayList<>();

    public static LavaOptions defaults(LavaMap owner) {
        return new LavaOptions(owner);
    }

    LavaOptions(LavaMap owner) {
        this.owner = owner;
    }

    public List<Vector> getSpawnPoints() {
        if (!isEnabled())
            return Arrays.asList(new Vector(owner.getLavaX(), owner.getLavaY(), owner.getLavaZ()));
        return spawnPoints;
    }

    public List<Location> getSpawnLocations() {
        if (!isEnabled())
            return Arrays.asList(owner.getLavaSpawnAsLocation());

        List<Location> list = new ArrayList<>();
        for (Vector vector : spawnPoints) {
            Location location = new Location(owner.getWorld(), vector.getX(), vector.getY(), vector.getZ());
            list.add(location);
        }

        return list;
    }

    public Vector getHighestSpawn() {
        if (!isEnabled())
            return owner.getLavaSpawnAsLocation().toVector();

        Vector highest = spawnPoints.get(0);
        for (Vector vector : spawnPoints)
            if (vector.getY() > highest.getY())
                highest = vector;

        return highest;
    }

    public Location getHighestLocation() {
        Vector highest = getHighestSpawn();
        return new Location(owner.getWorld(), highest.getX(), highest.getY(), highest.getZ());
    }

    public List<Location> getSpawnLocation(int xoffset, int yoffset, int zoffet) {
        if (!isEnabled())
            return Arrays.asList(owner.getLavaSpawnAsLocation(xoffset, yoffset, zoffet));

        List<Location> list = new ArrayList<>();
        for (Vector vector : spawnPoints) {
            Location location = new Location(owner.getWorld(), vector.getX() + xoffset, vector.getY() + yoffset, vector.getZ() + zoffet);
            list.add(location);
        }

        return list;
    }

    public Vector getSingleSpawn() {
        if (!isEnabled())
            return new Vector(owner.getLavaX(), owner.getLavaY(), owner.getLavaZ());
        else {
            if (selectedSpawn == null)
                selectedSpawn = spawnPoints.get(RANDOM.nextInt(spawnPoints.size()));
            return selectedSpawn;
        }
    }

    public Location getSingleSpawnLocation() {
        Vector spawn = getSingleSpawn();
        return new Location(owner.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
    }

    @Override
    public boolean isEnabled() {
        return spawnPoints.size() > 0 && super.isEnabled();
    }

    public void setParent(LavaMap parent) {
        this.owner = parent;
    }

    public boolean hasParent() {
        return this.owner != null;
    }
}