package me.eddiep.handles.logic;

import me.eddiep.ClassicPhysics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

abstract class AbstractLogicContainer implements LogicContainer {
    private final HashMap<World, Queue<Location>> worldQueues = new HashMap<>();
    private final HashMap<World, List<Location>> worldToAdd = new HashMap<>();
    private final List<World> unloadQueue = new LinkedList<>();
    private boolean ticking;

    @Override
    public synchronized void queueBlock(Location location) {
        World containingWorld = location.getWorld();
        if (Bukkit.getWorld(containingWorld.getName()) == null) {
            this.worldQueues.remove(containingWorld);
            return;
        }

        Queue<Location> queue;
        if (this.worldQueues.containsKey(containingWorld)) {
            queue = this.worldQueues.get(containingWorld);
        } else {
            queue = new ConcurrentLinkedDeque<>();
            this.worldQueues.put(containingWorld, queue);
        }

        if (this.ticking) {
            List<Location> toAdd;
            if (this.worldToAdd.containsKey(containingWorld))
                toAdd = this.worldToAdd.get(containingWorld);
            else {
                toAdd = new LinkedList<>();
                this.worldToAdd.put(containingWorld, toAdd);
            }
            toAdd.add(location);
        } else
            queue.offer(location);
    }

    @Override
    public synchronized void tick() {
        //Check unload queue for worlds that need to be unloaded
        for (World world : unloadQueue) {
            worldQueues.remove(world);
            worldToAdd.remove(world);
        }
        unloadQueue.clear();

        //Begin normal physics tick for each loaded world
        ticking = true;
        for (World world : worldQueues.keySet()) {
            Queue<Location> queue = worldQueues.get(world);
            if (queue == null)
                continue;

            while (!queue.isEmpty()) {
                Location location = queue.poll();
                if (location == null)
                    continue;
                tickForBlock(location);
            }
        }
        ticking = false;
        for (World world : worldQueues.keySet()) {
            Queue<Location> queue = worldQueues.get(world);
            List<Location> toAdd = worldToAdd.get(world);
            if (queue == null || toAdd == null)
                continue;
            queue.addAll(toAdd);
        }
        worldToAdd.clear();
    }

    @Override
    public void blockUpdate(Location location) {
        if (doesHandle(location.getBlock().getType()))
            queueBlock(location);
    }

    @Override
    public void unloadFor(World world) {
        unloadQueue.add(world);
    }

    @SuppressWarnings("unused")
    protected abstract void tickForBlock(Location location);

    void placeClassicBlock(Material material, Location location, Location from) {
        ClassicPhysics.INSTANCE.getPhysicsHandler().placeClassicBlockAt(location, material, from);
    }
}