package me.eddiep.handles;

import me.eddiep.ClassicPhysics;
import me.eddiep.PhysicsType;
import me.eddiep.handles.logic.LavaLogic;
import me.eddiep.handles.logic.LogicContainer;
import me.eddiep.handles.logic.WaterLogic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public final class ClassicPhysicsHandler implements Listener {
    private ArrayList<LogicContainerHolder> logicContainers = new ArrayList<>();

    private ArrayList<Player> lplacers = new ArrayList<>();
    private ArrayList<Player> wplacers = new ArrayList<>();
    private Plugin owner;
    private int taskId;
    private boolean blocking = false;

    private long tickCount;
    private final Runnable PHYSICS_TICK = new Runnable() {

        @Override
        public void run() {
            tickCount++;
            for (LogicContainerHolder holder : logicContainers) {

                if (tickCount - holder.lastTick >= holder.container.updateRate()) {
                    holder.container.tick();
                    holder.lastTick = tickCount;
                }
            }
        }
    };

    public ClassicPhysicsHandler(Plugin plugin) {
        this.owner = plugin;

        addLogicContainer(new LavaLogic());
        addLogicContainer(new WaterLogic());
    }

    public Plugin getOwner() {
        return owner;
    }

    public int getTaskId() {
        return taskId;
    }

    public boolean isPlacingLava(Player player) {
        return lplacers.contains(player);
    }

    public boolean isPlacingWater(Player player) {
        return wplacers.contains(player);
    }

    public void togglePlaceWater(Player player) {
        if (wplacers.contains(player))
            wplacers.remove(player);
        else
            wplacers.add(player);
    }

    public void togglePlaceLava(Player player) {
        if (lplacers.contains(player))
            lplacers.remove(player);
        else
            lplacers.add(player);
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (ClassicPhysics.TYPE == PhysicsType.DEFAULT)
            return;

        if (lplacers.contains(event.getPlayer())) {
            placeClassicBlockAt(event.getBlockPlaced().getLocation(), Material.LAVA);
            event.setCancelled(true);
        }
        else if (wplacers.contains(event.getPlayer())) {
            placeClassicBlockAt(event.getBlockPlaced().getLocation(), Material.WATER);
            event.setCancelled(true);
        }

        requestUpdateAround(event.getBlock().getLocation());
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (ClassicPhysics.TYPE == PhysicsType.DEFAULT)
            return;

        requestUpdateAround(event.getBlock().getLocation());
    }

    public void requestUpdateAround(Location location) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location newLoc = location.clone().add(x, y, z);
                    if (!newLoc.getBlock().hasMetadata("classic_block"))
                        continue;

                    for (LogicContainerHolder holder : logicContainers) {
                        holder.container.blockUpdate(newLoc);
                    }
                }
            }
        }
    }

    public void placeClassicBlockAt(Location location, Material type) {
        for (LogicContainerHolder holder : logicContainers) {
            if (holder.container.doesHandle(type)) {
                Block blc = location.getWorld().getBlockAt(location);

                if (!blc.hasMetadata("classic_block"))
                    blc.setMetadata("classic_block", new FixedMetadataValue(ClassicPhysics.INSTANCE, true));

                blc.setType(type);
                holder.container.queueBlock(blc);
                break; //TODO Maybe don't break?
            }
        }
    }

    public void addLogicContainer(LogicContainer container) {
        for (LogicContainerHolder holder : logicContainers) {
            if (holder.container.equals(container))
                return;
        }

        LogicContainerHolder holder = new LogicContainerHolder();
        holder.lastTick = 0;
        holder.container = container;
        logicContainers.add(holder);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPhysicsUpdate(BlockPhysicsEvent event) {
        if (ClassicPhysics.TYPE != PhysicsType.DEFAULT) {
            event.setCancelled(true);
            return;
        }

        /*Block checking = event.getBlock();

        Block north = checking.getRelative(BlockFace.NORTH);
        Block east = checking.getRelative(BlockFace.EAST);
        Block south = checking.getRelative(BlockFace.SOUTH);
        Block west = checking.getRelative(BlockFace.WEST);
        Block down = checking.getRelative(BlockFace.DOWN);
        Block up = checking.getRelative(BlockFace.UP);

        if (validClassicBlocks.contains(checking.getType()) && checking.hasMetadata("classicBlock")) {
            if (checking.getRelative(BlockFace.NORTH).getType().equals(Material.AIR))
                updatePhys(checking.getRelative(BlockFace.NORTH), checking.getType());
            if (checking.getRelative(BlockFace.EAST).getType().equals(Material.AIR))
                updatePhys(checking.getRelative(BlockFace.EAST), checking.getType());
            if (checking.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR))
                updatePhys(checking.getRelative(BlockFace.SOUTH), checking.getType());
            if (checking.getRelative(BlockFace.WEST).getType().equals(Material.AIR))
                updatePhys(checking.getRelative(BlockFace.WEST), checking.getType());
            if (checking.getRelative(ClassicPhysics.TYPE.equals(PhysicsType.REVERSE) ? BlockFace.UP : BlockFace.DOWN).getType().equals(Material.AIR))
                updatePhys(checking.getRelative(ClassicPhysics.TYPE.equals(PhysicsType.REVERSE) ? BlockFace.UP : BlockFace.DOWN), checking.getType());
        }

        if (validClassicBlocks.contains(down.getType()) || (ClassicPhysics.TYPE.equals(PhysicsType.REVERSE) && validClassicBlocks.contains(up.getType()))) {
            event.setCancelled(true);
            return;
        }

        if (validClassicBlocks.contains(north.getType()))
            event.setCancelled(!handleEvent(event, north, BlockFace.NORTH));
        if (validClassicBlocks.contains(east.getType()))
            event.setCancelled(!handleEvent(event, east, BlockFace.EAST));
        if (validClassicBlocks.contains(south.getType()))
            event.setCancelled(!handleEvent(event, south, BlockFace.SOUTH));
        if (validClassicBlocks.contains(west.getType()))
            event.setCancelled(!handleEvent(event, west, BlockFace.WEST));
        if (validClassicBlocks.contains(up.getType()))
            event.setCancelled(!handleEvent(event, up, BlockFace.UP));*/
    }

    public void enable() {
        taskId = owner.getServer().getScheduler().scheduleSyncRepeatingTask(owner, PHYSICS_TICK, 0, 1);
        owner.getServer().getPluginManager().registerEvents(this, owner);
    }

    public void disable() {
        BlockPhysicsEvent.getHandlerList().unregister(this);
        owner.getServer().getScheduler().cancelTask(taskId);
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

    @Deprecated
    public boolean handleEvent(BlockPhysicsEvent event, Block newBlock, BlockFace direction) {
        /*event.setCancelled(true);
        final Block block = event.getBlock();

        if (*//*this.blocking || *//*block.getType() == newBlock.getType())
            return true;

        ClassicPhysicsEvent cevent;
        if (block.getType() == Material.AIR) {
            cevent = new ClassicPhysicsEvent(event, newBlock.getType(), newBlock.hasMetadata("classicBlock"));
            owner.getServer().getPluginManager().callEvent(cevent);
            if (cevent.isCancelled() || (validClassicBlocks.contains(newBlock.getType()) && !cevent.isClassicEvent()))
                return true;
        } else {
            cevent = new ClassicPhysicsEvent(event, newBlock.getType(), block.hasMetadata("classicBlock"));
            cevent.setCancelled(true);
            owner.getServer().getPluginManager().callEvent(cevent);
            if (cevent.isCancelled())
                return true;
        }

        final Material type = cevent.getNewBlock();

        QueuedBlock qblock = new QueuedBlock(block.getX(), block.getY(), block.getZ(), type, block.getWorld(), System.nanoTime());
        if (physicBlocks.size() >= MAX_QUEUE_SIZE) {
            if (!this.blocking)
                ClassicPhysics.INSTANCE.log("To many queued blocks! Rejecting all future queued blocks until queueing empties");
            this.blocking = true;
            if (!block.hasMetadata("classicBlock"))
                block.setMetadata("classicBlock", new FixedMetadataValue(ClassicPhysics.INSTANCE, true));
            return true;
        }
        physicBlocks.add(qblock);*/
        return false;
    }

    private class LogicContainerHolder {
        long lastTick;
        LogicContainer container;
    }
}