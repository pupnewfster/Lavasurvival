package me.eddiep.handles;

import me.eddiep.ClassicPhysics;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class ClassicPhysicsHandler implements Listener {
    private ArrayList<QueuedBlock> physicBlocks = new ArrayList<QueuedBlock>();
    private ArrayList<Material> validClassicBlocks = new ArrayList<Material>();
    private Plugin owner;
    private int taskId;
    private boolean blocking = false;
    private static final int MAX_QUEUE_SIZE = 7000;

    public ClassicPhysicsHandler(Plugin plugin) {
        this.owner = plugin;

        validClassicBlocks.add(Material.LAVA);
        validClassicBlocks.add(Material.STATIONARY_LAVA);
        validClassicBlocks.add(Material.WATER);
        validClassicBlocks.add(Material.STATIONARY_WATER);
    }

    public Plugin getOwner() {
        return owner;
    }

    public int getTaskId() {
        return taskId;
    }

    @EventHandler
    public void onClassicPhysics(ClassicPhysicsEvent event) {
        ClassicPhysicsHandler handler = ClassicPhysics.INSTANCE.getPhysicsHandler();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPhysicsUpdate(BlockPhysicsEvent event) {
        Block checking = event.getBlock();
        Material east = checking.getRelative(BlockFace.EAST).getType();
        Material north = checking.getRelative(BlockFace.NORTH).getType();
        Material south = checking.getRelative(BlockFace.SOUTH).getType();
        Material west = checking.getRelative(BlockFace.WEST).getType();
        Material down = checking.getRelative(BlockFace.DOWN).getType();
        Material up = checking.getRelative(BlockFace.UP).getType();

        if (validClassicBlocks.contains(down)) {
            event.setCancelled(true);
            return;
        }

        if (validClassicBlocks.contains(east))
            handleEvent(event, east);
        if (validClassicBlocks.contains(north))
            handleEvent(event, north);
        if (validClassicBlocks.contains(south))
            handleEvent(event, south);
        if (validClassicBlocks.contains(west))
            handleEvent(event, west);
        if (validClassicBlocks.contains(up))
            handleEvent(event, up);
    }

    public List<Material> getValidPhysicBlocks() {
        return Collections.unmodifiableList(validClassicBlocks);
    }

    public void addPhysicsBlock(Material material) {
        validClassicBlocks.add(material);
    }

    public void removePhysicsBlock(Material material) {
        validClassicBlocks.remove(material);
    }

    public void enable() {
        taskId = owner.getServer().getScheduler().scheduleSyncRepeatingTask(owner, PHYSICS_TICK, 0, 1);
        owner.getServer().getPluginManager().registerEvents(this, owner);
    }

    public void disable() {
        BlockPhysicsEvent.getHandlerList().unregister(this);
    }

    public void setPhysicSpeed(World world, long speed) {
        world.setMetadata("physicsSpeed", new FixedMetadataValue(owner, speed));

        owner.getLogger().info("Physic speed in world " + world.getName() + " set to " + speed + "ms!");
    }

    public void setPhysicLevel(World world, int level) {
        world.setMetadata("physicsLevel", new FixedMetadataValue(owner, level));

        owner.getLogger().info("Physic level in world " + world.getName() + " set to " + level + "!");
    }

    public long getPhysicsSpeed(World world) {
        return world.getMetadata("physicsSpeed").get(0).asLong();
    }

    public int getPhysicsLevel(World world) {
        return world.getMetadata("physicsLevel").get(0).asInt();
    }

    public boolean hasPhysicsSpeed(World world) {
        return world.getMetadata("physicsSpeed").size() > 0;
    }

    public boolean hasPhysicsLevel(World world) {
        return world.getMetadata("physicsLevel").size() > 0;
    }

    public void handleEvent(BlockPhysicsEvent event, Material newBlock) {
        event.setCancelled(true);
        Block block = event.getBlock();

        if (this.blocking || block.getType() == newBlock)
            return;

        ClassicPhysicsEvent cevent = new ClassicPhysicsEvent(event, newBlock);

        cevent.setCancelled(block.getType().isSolid());

        owner.getServer().getPluginManager().callEvent(cevent);
        if (cevent.isCancelled()) {
            return;
        }

        newBlock = cevent.getNewBlock();

        QueuedBlock qblock = new QueuedBlock(block.getX(), block.getY(), block.getZ(), newBlock, block.getWorld(), System.nanoTime());
        if (physicBlocks.size() >= MAX_QUEUE_SIZE) {
            if (!this.blocking)
                ClassicPhysics.INSTANCE.log("To many queued blocks! Rejecting all future queued blocks until queueing empties");
            this.blocking = true;
            return;
        }
        physicBlocks.add(qblock);
    }

    private final Runnable PHYSICS_TICK = new Runnable() {

        @Override
        public void run() {
            ArrayList<QueuedBlock> toPlace = new ArrayList<QueuedBlock>();
            Iterator<QueuedBlock> blocks = physicBlocks.iterator();
            while (blocks.hasNext()) {
                QueuedBlock block = blocks.next();
                World world = block.getWorld();
                if (!hasPhysicsSpeed(world)) {
                    setPhysicSpeed(world, 800);
                }
                if (!hasPhysicsLevel(world)) {
                    setPhysicLevel(world, 1);
                }
                long speed = world.getMetadata("physicsSpeed").get(0).asLong(); //I should be safe with this...
                long now = System.nanoTime();

                long timePassed = (now - block.getTickOccurred()) / 1000000;
                if (timePassed >= speed) {
                    toPlace.add(block);
                    blocks.remove();
                }
            }

            for (QueuedBlock block : toPlace) {
                World world = block.getWorld();
                world.getBlockAt(block.getX(), block.getY(), block.getZ()).setType(block.getBlockType());
            }
            toPlace.clear();

            if (blocking) {
                if (physicBlocks.size() == 0)
                    blocking = false;
            }
        }
    };

    private class QueuedBlock {
        private int x;
        private int y;
        private int z;
        private Material blockType;
        private long tickOccurred;
        private World world;

        public QueuedBlock(int x, int y, int z, Material blockType, World world, long tickOccurred) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockType = blockType;
            this.tickOccurred = tickOccurred;
            this.world = world;
        }

        public World getWorld() {
            return world;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
        public int getZ() {
            return z;
        }

        public Material getBlockType() {
            return blockType;
        }

        public long getTickOccurred() {
            return tickOccurred;
        }
    }
}
