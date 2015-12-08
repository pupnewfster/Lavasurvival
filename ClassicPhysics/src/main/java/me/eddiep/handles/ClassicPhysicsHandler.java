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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public final class ClassicPhysicsHandler implements Listener {
    private ArrayList<LogicContainerHolder> logicContainers = new ArrayList<>();
    private final HashMap<Location, Material> locations = new HashMap<>();
    private ArrayList<Player> lplacers = new ArrayList<>();
    private ArrayList<Player> wplacers = new ArrayList<>();
    private Plugin owner;

    private long tickCount;
    private final BukkitRunnable PHYSICS_TICK = new BukkitRunnable() {
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

    private final BukkitRunnable BLOCK_UPDATE_TICK = new BukkitRunnable() {
        @Override
        public void run() {
            synchronized (locations) {
                HashMap<Location, Material> temp = (HashMap<Location, Material>) locations.clone();
                for (Location l : temp.keySet()) {
                    if (l.getWorld() == null)//World isn't loaded
                        continue;
                    Block blc = l.getBlock();
                    Material type = temp.get(l);
                    if (!blc.hasMetadata("classic_block"))
                        blc.setMetadata("classic_block", new FixedMetadataValue(ClassicPhysics.INSTANCE, true));
                    blc.setType(type);
                    for (LogicContainerHolder holder : logicContainers) {
                        if (holder.container.doesHandle(type)) {
                            holder.container.queueBlock(blc);
                            break; //TODO Maybe don't break?
                        }
                    }
                    locations.remove(l);
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        if (event.isCancelled())
            return;

        for (LogicContainerHolder holder : logicContainers)
            holder.container.unloadFor(event.getWorld());
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (ClassicPhysics.TYPE == PhysicsType.DEFAULT)
            return;

        if (lplacers.contains(event.getPlayer())) {
            forcePlaceClassicBlockAt(event.getBlockPlaced().getLocation(), Material.LAVA);
            event.setCancelled(true);
        } else if (wplacers.contains(event.getPlayer())) {
            forcePlaceClassicBlockAt(event.getBlockPlaced().getLocation(), Material.WATER);
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

    @EventHandler
    public void onIceMelt(BlockFadeEvent event) {
        if (ClassicPhysics.TYPE == PhysicsType.DEFAULT)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        if (ClassicPhysics.TYPE == PhysicsType.DEFAULT)
            return;
        event.setCancelled(true);
    }

    public void requestUpdateAround(Location location) {
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    Location newLoc = location.clone().add(x, y, z);
                    if (!newLoc.getBlock().hasMetadata("classic_block"))
                        continue;
                    for (LogicContainerHolder holder : logicContainers)
                        holder.container.blockUpdate(newLoc);
                }
    }

    public void forcePlaceClassicBlockAt(Location location, final Material type) {//Force place block
        if (location.getWorld() == null)//World isn't loaded
            return;
        for (LogicContainerHolder holder : logicContainers)
            if (holder.container.doesHandle(type)) {
                final Block blc = location.getBlock();
                if (!blc.hasMetadata("classic_block"))
                    blc.setMetadata("classic_block", new FixedMetadataValue(ClassicPhysics.INSTANCE, true));
                blc.setType(type);
                holder.container.queueBlock(blc);
                break; //TODO Maybe don't break?
            }
    }

    public void placeClassicBlockAt(Location location, final Material type) {
        if (location.getWorld() == null)//World isn't loaded
            return;
        for (LogicContainerHolder holder : logicContainers)
            if (holder.container.doesHandle(type)) {
                locations.put(location, type);
                break;
            }
    }

    public void addLogicContainer(LogicContainer container) {
        for (LogicContainerHolder holder : logicContainers)
            if (holder.container.equals(container))
                return;
        LogicContainerHolder holder = new LogicContainerHolder();
        holder.lastTick = 0;
        holder.container = container;
        logicContainers.add(holder);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPhysicsUpdate(BlockPhysicsEvent event) {
        if (ClassicPhysics.TYPE == PhysicsType.DEFAULT)
            return;
        if (!event.getBlock().getType().toString().contains("DOOR"))
            event.setCancelled(true);
    }

    public void enable() {
        PHYSICS_TICK.runTaskTimerAsynchronously(owner, 0, 1);
        BLOCK_UPDATE_TICK.runTaskTimer(owner, 0, 20);//TODO: ability to change update rate of blocks
        owner.getServer().getPluginManager().registerEvents(this, owner);
    }

    public void disable() {
        BlockPhysicsEvent.getHandlerList().unregister(this);
        PHYSICS_TICK.cancel();
        BLOCK_UPDATE_TICK.cancel();
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

    private class LogicContainerHolder {
        long lastTick;
        LogicContainer container;
    }
}