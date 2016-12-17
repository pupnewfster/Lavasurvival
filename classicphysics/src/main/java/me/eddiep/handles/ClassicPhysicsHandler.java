package me.eddiep.handles;

import me.eddiep.ChunkEdit;
import me.eddiep.ClassicPhysics;
import me.eddiep.PhysicsType;
import me.eddiep.handles.logic.LavaLogic;
import me.eddiep.handles.logic.LogicContainer;
import me.eddiep.handles.logic.WaterLogic;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("unused")
public final class ClassicPhysicsHandler implements Listener {
    private final ArrayList<LogicContainerHolder> logicContainers = new ArrayList<>();
    private final ConcurrentHashMap<ToAndFrom, Material> locations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, WorldCount> chunks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Location, ConcurrentLinkedQueue<ToAndFrom>> toFroms = new ConcurrentHashMap<>();
    private final ArrayList<Player> lplacers = new ArrayList<>();
    private final ArrayList<Player> wplacers = new ArrayList<>();
    private boolean running = false, removePrevious = false;
    private World current = null;
    private ChunkEdit e = null;
    private final Plugin owner;

    @SuppressWarnings("unused")
    private class WorldCount {
        private final ArrayList<Short> changes = new ArrayList<>();
        private final World world;
        private int x, y, z;
        private final long l;

        WorldCount(World world, long l) {
            this.world = world;
            this.l = l;
        }

        void addChange(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            int blockX = x % 16, blockZ = z % 16;
            if (blockX < 0)
                blockX += 16;
            if (blockZ < 0)
                blockZ += 16;
            short loc = (short) ((blockX << 12) | (blockZ << 8) | (y));
            if (!this.changes.contains(loc))
                this.changes.add(loc);
        }

        public World getWorld() {
            return this.world;
        }

        private short[] getChanged(List<Short> changes) {
            short[] temp = new short[changes.size()];
            for (int i = 0; i < changes.size(); i++)
                temp[i] = changes.get(i);
            return temp;
        }

        List<Packet> getPackets() {
            List<Packet> packets = new ArrayList<>();
            if (this.world == null || this.changes.size() == 0)
                return null;
            int size = this.changes.size();
            if (size == 1)
                packets.add(new PacketPlayOutBlockChange(((CraftWorld) this.world).getHandle(), new BlockPosition(this.x, this.y, this.z)));
            else if (size == 64)
                packets.add(new PacketPlayOutMapChunk(((CraftWorld) this.world).getHandle().getChunkAt((int) (this.l >> 32), (int) this.l), size));
            else if (size < 64)
                packets.add(new PacketPlayOutMultiBlockChange(size, getChanged(this.changes), ((CraftWorld) this.world).getHandle().getChunkAt((int) (this.l >> 32), (int) this.l)));
            else {
                int i = 0, end;
                while (i < size) {
                    end = i + 64 > size ? size : i + 64;
                    packets.add(new PacketPlayOutMultiBlockChange(end - i, getChanged(this.changes.subList(i, end)), ((CraftWorld) this.world).getHandle().getChunkAt((int) (this.l >> 32), (int) this.l)));
                    i += 64;
                }
            }
            return packets;
        }

        int getX() {
            return this.x;
        }

        int getY() {
            return this.y;
        }

        int getZ() {
            return this.z;
        }
    }

    private class ToAndFrom {
        final Location from;
        final Location to;

        ToAndFrom(Location to, Location from) {
            this.to = to;
            this.from = from;
        }

        public Location getTo() {
            return this.to;
        }

        public Location getFrom() {
            return this.from;
        }
    }

    private long tickCount;
    private final BukkitRunnable PHYSICS_TICK = new BukkitRunnable() {
        @Override
        public void run() {
            tickCount++;
            logicContainers.stream().filter(holder -> tickCount - holder.lastTick >= holder.container.updateRate()).forEach(holder -> {
                holder.container.tick();
                holder.lastTick = tickCount;
            });
        }
    };

    private final BukkitRunnable BLOCK_UPDATE_TICK = new BukkitRunnable() {
        @Override
        public void run() {
            if (running) //TODO: See if some of this can be made async to improve behavior with large amounts of block updates
                return;
            running = true;
            for (ToAndFrom taf : locations.keySet()) {
                if (!running)
                    break;
                Location l = taf.getTo();
                if (l.getWorld() == null || !l.getChunk().isLoaded() || l.getBlock() == null) {//World isn't loaded
                    ConcurrentLinkedQueue<ToAndFrom> queue = toFroms.get(l);
                    if (queue != null) {
                        while (!queue.isEmpty()) {
                            ToAndFrom t = queue.poll();
                            if (t != null)
                                locations.remove(t);
                        }
                    }
                    toFroms.remove(l);
                    locations.remove(taf);
                    continue;
                }
                Block blc = l.getBlock();
                if (current == null || !current.equals(blc.getWorld())) {
                    e = new ChunkEdit(((CraftWorld) blc.getWorld()).getHandle());
                    current = blc.getWorld();
                }
                Material type = locations.get(taf);
                if (!taf.getFrom().getBlock().hasMetadata("classic_block") || !taf.getFrom().getBlock().isLiquid() || type == null || (blc.hasMetadata("classic_block") && blc.isLiquid())) {
                    ConcurrentLinkedQueue<ToAndFrom> queue = toFroms.get(l);
                    if (queue != null) {
                        while (!queue.isEmpty()) {
                            ToAndFrom t = queue.poll();
                            if (t != null)
                                locations.remove(t);
                        }
                    }
                    toFroms.remove(l);
                    locations.remove(taf);
                    continue;
                }
                if (!blc.hasMetadata("classic_block"))
                    blc.setMetadata("classic_block", new FixedMetadataValue(ClassicPhysics.INSTANCE, true));
                e.setBlock(blc.getX(), blc.getY(), blc.getZ(), type);
                long xz = (long) blc.getChunk().getX() << 32 | blc.getChunk().getZ() & 0xFFFFFFFFL;
                if (!chunks.containsKey(xz))
                    chunks.put(xz, new WorldCount(l.getWorld(), xz));
                chunks.get(xz).addChange(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                if (!blc.isLiquid() && !blc.hasMetadata("fusion_block")) {
                    type = Material.STATIONARY_WATER;
                    e.setBlock(blc.getX(), blc.getY(), blc.getZ(), type);
                }
                ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(new ClassicBlockPlaceEvent(l));
                for (LogicContainerHolder holder : logicContainers) {
                    if (holder.container.doesHandle(type)) {
                        holder.container.queueBlock(blc.getLocation());
                        break;
                    }
                }
                ConcurrentLinkedQueue<ToAndFrom> queue = toFroms.get(l);
                if (queue != null) {
                    while (!queue.isEmpty()) {
                        ToAndFrom t = queue.poll();
                        if (t != null)
                            locations.remove(t);
                    }
                }
                toFroms.remove(l);
                locations.remove(taf);
            }
            ArrayList<Packet> packets = new ArrayList<>();
            if (!Bukkit.getOnlinePlayers().isEmpty())
                for (long l : chunks.keySet()) {
                    if (!running)
                        break;
                    packets.addAll(chunks.get(l).getPackets());
                    chunks.remove(l);
                }
            else
                chunks.clear();
            if (!packets.isEmpty()) //TODO figure out why sometimes the packet does not get sent. Is it some over max amount?
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (removePrevious)
                        break;
                    if (p != null) {
                        final EntityPlayer ep = ((CraftPlayer) p).getHandle();
                        for (Packet packet : packets) {
                            if (removePrevious)//Check again in case player is mid getting sent
                                break;
                            ep.playerConnection.sendPacket(packet);
                        }
                    }
                }
            if (removePrevious)
                removePrevious = false;
            running = false;
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

        logicContainers.forEach(holder -> holder.container.unloadFor(event.getWorld()));

        this.toFroms.clear();//Because we don't call block placing in multiple worlds. If we ever start we need to make it check that it removes correct worlds
        this.chunks.clear();
        if (running)
            running = false;
        if (!removePrevious)
            removePrevious = true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (ClassicPhysics.TYPE == PhysicsType.DEFAULT)
            return;
        if (lplacers.contains(event.getPlayer())) {
            forcePlaceClassicBlockAt(event.getBlockPlaced().getLocation(), Material.STATIONARY_LAVA);
            event.setCancelled(true);
        } else if (wplacers.contains(event.getPlayer())) {
            forcePlaceClassicBlockAt(event.getBlockPlaced().getLocation(), Material.STATIONARY_WATER);
            event.setCancelled(true);
        }
        if (event.getBlock().hasMetadata("classic_block"))
            event.getBlock().removeMetadata("classic_block", ClassicPhysics.INSTANCE);
        requestUpdateAround(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            requestUpdateAround(event.getBlock().getLocation());
    }

    @EventHandler
    public void onIceMelt(BlockFadeEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            event.setCancelled(true);
    }

    @EventHandler
    public void onCactusGrow(BlockGrowEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            event.setCancelled(true);
    }

    @EventHandler
    public void fireSpread(BlockIgniteEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            event.setCancelled(true);
    }

    @EventHandler
    public void blockFall(EntityChangeBlockEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT) && event.getEntity() instanceof FallingBlock) {
            event.setCancelled(true);
            event.getBlock().getState().update(true, false);
        }
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        if (!ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        if (ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            return;
        event.setCancelled(true);
        if (event.getToBlock().hasMetadata("classic_block") && event.getBlock().hasMetadata("classic_block"))//To is really the from block it is labelled strangely
            placeClassicBlockAt(event.getBlock().getLocation(), event.getToBlock().getType(), event.getToBlock().getLocation());
    }

    private void requestUpdateAround(Location location) {
        try {
            if (location == null || location.getWorld() == null || location.getChunk() == null || !location.getChunk().isLoaded() || location.getBlock() == null)//World isn't loaded
                return;
        } catch (Exception e) {
            return;
        }
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    Location newLoc = location.clone().add(x, y, z);
                    if (!newLoc.getBlock().hasMetadata("classic_block"))//|| !newLoc.getBlock().isLiquid()
                        continue;
                    for (LogicContainerHolder holder : logicContainers)
                        if (holder.container.doesHandle(newLoc.getBlock().getType())) {
                            holder.container.queueBlock(newLoc);
                            break;
                        }
                }
    }

    public void forcePlaceClassicBlockAt(Location location, Material type) {//Force place block
        try {
            if (location == null || location.getWorld() == null || location.getChunk() == null || !location.getChunk().isLoaded() || location.getBlock() == null)//World isn't loaded
                return;
        } catch (Exception e) {
            return;
        }
        if (type.equals(Material.WATER))
            type = Material.STATIONARY_WATER;
        else if (type.equals(Material.LAVA))
            type = Material.STATIONARY_LAVA;
        Block blc = location.getBlock();
        if (!blc.hasMetadata("classic_block"))
            blc.setMetadata("classic_block", new FixedMetadataValue(ClassicPhysics.INSTANCE, true));
        if (current == null || !current.equals(blc.getWorld())) {
            e = new ChunkEdit(((CraftWorld) blc.getWorld()).getHandle());
            current = blc.getWorld();
        }
        e.setBlock(blc.getX(), blc.getY(), blc.getZ(), type);
        if (!blc.isLiquid() && !blc.hasMetadata("fusion_block")) {
            type = Material.STATIONARY_WATER;
            e.setBlock(blc.getX(), blc.getY(), blc.getZ(), type);
        }
        ClassicPhysics.INSTANCE.getServer().getPluginManager().callEvent(new ClassicBlockPlaceEvent(location));
        for (LogicContainerHolder holder : logicContainers)
            if (holder.container.doesHandle(type)) {
                holder.container.queueBlock(blc.getLocation());
                break;
            }
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld) location.getWorld()).getHandle(), new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        Bukkit.getOnlinePlayers().stream().filter(Objects::nonNull).forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
    }

    public void placeClassicBlockAt(Location location, Material type, Location from) {
        try {
            if (location == null || location.getWorld() == null || location.getChunk() == null || !location.getChunk().isLoaded() || location.getBlock() == null)//World isn't loaded
                return;
        } catch (Exception e) {
            return;
        }
        if (type.equals(Material.WATER))
            type = Material.STATIONARY_WATER;
        else if (type.equals(Material.LAVA))
            type = Material.STATIONARY_LAVA;
        for (LogicContainerHolder holder : logicContainers)
            if (holder.container.doesHandle(type)) {
                ToAndFrom taf = new ToAndFrom(location, from);
                locations.put(taf, type);
                ConcurrentLinkedQueue<ToAndFrom> temp = new ConcurrentLinkedQueue<>();
                if (toFroms.containsKey(location) && toFroms.get(location) != null && toFroms.get(location).size() > 0)
                    temp = toFroms.get(location);
                temp.add(taf);
                toFroms.put(location, temp);
                break;
            }
    }

    private void addLogicContainer(LogicContainer container) {
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
        if (ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            return;
        if (!event.getBlock().getType().toString().contains("DOOR") || !event.getChangedType().toString().contains("PLATE") || (event.getBlock().getType().toString().contains("DOOR") &&
                event.getChangedType().toString().contains("PLATE") && !event.getBlock().getType().equals(event.getBlock().getRelative(BlockFace.UP).getType())))
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