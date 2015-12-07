package me.eddiep.handles.logic;

import me.eddiep.ClassicPhysics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class AbstractLogicContainer implements LogicContainer {
    private boolean ticking;
    //new ConcurrentLinkedQueue<>
    private HashMap<World, Queue<Block>> worldQueues = new HashMap<>();
    private HashMap<World, List<Block>> worldToAdd = new HashMap<>();
    private List<World> unloadQueue = new LinkedList<>();

    @Override
    public void queueBlock(Block block) {
        World containingWorld = block.getWorld();
        if (Bukkit.getWorld(containingWorld.getName()) == null) {
            worldQueues.remove(containingWorld);
            return;
        }

        Queue<Block> queue;
        if (this.worldQueues.containsKey(containingWorld)) {
            queue = this.worldQueues.get(containingWorld);
        } else {
            queue = new ConcurrentLinkedDeque<>();
            this.worldQueues.put(containingWorld, queue);
        }

        if (ticking) {
            List<Block> toAdd;
            if (worldToAdd.containsKey(containingWorld)) {
                toAdd = worldToAdd.get(containingWorld);
            } else {
                toAdd = new LinkedList<>();
                worldToAdd.put(containingWorld, toAdd);
            }

            toAdd.add(block);

        } else {
            queue.offer(block);
        }
    }

    @Override
    public void tick() {
        //Check unload queue for worlds that need to be unloaded
        for (World world : unloadQueue) {
            worldQueues.remove(world);
            worldToAdd.remove(world);
        }
        unloadQueue.clear();

        //Begin normal physics tick for each loaded world
        ticking = true;
        for (World world : worldQueues.keySet()) {
            Queue<Block> queue = worldQueues.get(world);
            if (queue == null)
                continue;

            while (!worldQueues.isEmpty()) {
                Block block = queue.poll();
                if (block == null)
                    continue;


                tickForBlock(block, block.getLocation());
            }
        }
        ticking = false;

        for (World world : worldQueues.keySet()) {
            Queue<Block> queue = worldQueues.get(world);
            List<Block> toAdd = worldToAdd.get(world);
            if (queue == null)
                continue;

            queue.addAll(toAdd);
        }

        worldToAdd.clear();
    }

    @Override
    public void blockUpdate(Location location) {
        Material material = location.getBlock().getType();
        if (doesHandle(material))
            queueBlock(location.getBlock());
    }

    @Override
    public void unloadFor(World world) {
        unloadQueue.add(world);
    }

    protected abstract void tickForBlock(Block block, Location location);

    protected void placeClassicBlock(Material material, Location location) {
        ClassicPhysics.INSTANCE.getPhysicsHandler().placeClassicBlockAt(location, material);
    }
}
