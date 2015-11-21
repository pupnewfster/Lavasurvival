package me.eddiep.handles.logic;

import me.eddiep.ClassicPhysics;
import me.eddiep.handles.ClassicPhysicsEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractLogicContainer implements LogicContainer {
    private boolean ticking;
    private Queue<Block> queue = new ConcurrentLinkedQueue<>();
    private List<Block> toAdd = new LinkedList<>();

    @Override
    public void queueBlock(Block block) {
        if (ticking) {
            toAdd.add(block);
        } else {
            queue.offer(block);
        }
    }

    @Override
    public void tick() {
        while (!queue.isEmpty()) {
            Block block = queue.poll();
            if (block == null)
                continue;


            tickForBlock(block, block.getLocation());
        }

        queue.addAll(toAdd);
        toAdd.clear();
    }

    protected abstract void tickForBlock(Block block, Location location);

    protected void placeClassicBlock(Material material, Location location) {
        ClassicPhysicsEvent event = new ClassicPhysicsEvent(location.getBlock(), material, true);
        ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(event);

        ClassicPhysics.INSTANCE.getPhysicsHandler().placeClassicBlockAt(location, material);
    }
}
