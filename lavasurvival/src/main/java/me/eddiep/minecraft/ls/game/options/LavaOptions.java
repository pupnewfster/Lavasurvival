package me.eddiep.minecraft.ls.game.options;

import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LavaOptions extends BaseOptions {
    private transient Vector selectedSpawn;
    private transient LavaMap owner;

    private List<Vector> spawnPoints = new ArrayList<>();

    public static LavaOptions defaults(LavaMap owner) {
        return new LavaOptions(owner);
    }

    private LavaOptions(LavaMap owner) {
        this.owner = owner;
    }

    public List<Vector> getSpawnPoints() {
        if (!isEnabled())
            return Collections.singletonList(new Vector(this.owner.getLavaX(), this.owner.getLavaY(), this.owner.getLavaZ()));
        return this.spawnPoints;
    }

    public List<Location> getSpawnLocations() {
        if (!isEnabled())
            return Collections.singletonList(this.owner.getLavaSpawnAsLocation());
        return this.spawnPoints.stream().map(vector -> new Location(this.owner.getWorld(), vector.getX(), vector.getY(), vector.getZ())).collect(Collectors.toList());
    }

    private Vector getHighestSpawn() {
        if (!isEnabled())
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

    public List<Location> getSpawnLocation(int xoffset, int yoffset, int zoffet) {
        if (!isEnabled())
            return Collections.singletonList(this.owner.getLavaSpawnAsLocation(xoffset, yoffset, zoffet));
        return this.spawnPoints.stream().map(vector -> new Location(this.owner.getWorld(), vector.getX() + xoffset, vector.getY() + yoffset, vector.getZ() + zoffet)).collect(Collectors.toList());
    }

    private Vector getSingleSpawn() {
        if (!isEnabled())
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

    @Override
    public boolean isEnabled() {
        return this.spawnPoints.size() > 0 && super.isEnabled();
    }

    public void setParent(LavaMap parent) {
        this.owner = parent;
    }

    public boolean hasParent() {
        return this.owner != null;
    }
}