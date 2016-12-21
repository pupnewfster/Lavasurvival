package me.eddiep.handles.logic;

import me.eddiep.ClassicPhysics;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract class AbstractLogicContainer implements LogicContainer {
    private final Queue<Location> queue = new ConcurrentLinkedQueue<>(); //Does this need to be concurrent
    private final Queue<Location> toAdd = new ConcurrentLinkedQueue<>();
    private boolean ticking;

    @Override
    public void queueBlock(Location location) {
        if (location == null)
            return;
        if (this.ticking)
            this.toAdd.offer(location);
        else
            this.queue.offer(location);
    }

    @Override
    public synchronized void tick() {
        //Begin normal physics tick for each loaded world
        this.ticking = true;
        while (!this.queue.isEmpty())
            tickForBlock(this.queue.poll());
        this.ticking = false;
        if (!this.toAdd.isEmpty()) {
            this.queue.addAll(this.toAdd);
            this.toAdd.clear();
        }
    }

    @Override
    public void unload() {
        toAdd.clear();
        queue.clear();
    }

    protected abstract void tickForBlock(Location location);

    void placeClassicBlock(Material material, Location location, Location from) {
        ClassicPhysics.INSTANCE.getPhysicsHandler().placeClassicBlockAt(location, material, from);
    }
}