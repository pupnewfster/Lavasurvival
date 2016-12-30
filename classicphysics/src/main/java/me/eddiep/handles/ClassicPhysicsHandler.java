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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ClassicPhysicsHandler implements Listener {
    private final ArrayList<LogicContainerHolder> logicContainers = new ArrayList<>();
    private final ConcurrentHashMap<Location, ConcurrentLinkedQueue<Location>> locations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, WorldCount> chunks = new ConcurrentHashMap<>();
    private final HashSet<BlockVector> classicBlocks = new HashSet<>(), playerPlaced = new HashSet<>();
    private final ArrayList<Player> lplacers = new ArrayList<>();
    private final ArrayList<Player> wplacers = new ArrayList<>();
    private boolean running = false, hasPlayers = false, sendingPackets  = false;
    private World current = null;
    private ChunkEdit e = null;
    private final Plugin owner;

    @SuppressWarnings("unused")
    private class WorldCount {
        private final ArrayList<Short> changes = new ArrayList<>();
        private int x, y, z;
        private final long l;

        WorldCount(long l) {
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

        private short[] getChanged(List<Short> changes) {
            short[] temp = new short[changes.size()];
            for (int i = 0; i < changes.size(); i++)
                temp[i] = changes.get(i);
            return temp;
        }

        List<Packet> getPackets() {
            List<Packet> packets = new ArrayList<>();
            if (current == null || this.changes.size() == 0)
                return null;
            int size = this.changes.size();
            if (size == 1)
                packets.add(new PacketPlayOutBlockChange(((CraftWorld) current).getHandle(), new BlockPosition(this.x, this.y, this.z)));
            else if (size == 64)
                packets.add(new PacketPlayOutMapChunk(((CraftWorld) current).getHandle().getChunkAt((int) (this.l >> 32), (int) this.l), size));
            else if (size < 64)
                packets.add(new PacketPlayOutMultiBlockChange(size, getChanged(this.changes), ((CraftWorld) current).getHandle().getChunkAt((int) (this.l >> 32), (int) this.l)));
            else {
                int i = 0, end;
                while (i < size) {
                    end = i + 64 > size ? size : i + 64;
                    packets.add(new PacketPlayOutMultiBlockChange(end - i, getChanged(this.changes.subList(i, end)), ((CraftWorld) current).getHandle().getChunkAt((int) (this.l >> 32), (int) this.l)));
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
            if (running || current == null)
                return;
            //TODO: Attempt to make the logic of what blocks should be updated smarter so that it stays smoother
            //TODO: How many of these need to be concurrent does the linked queues?
            //TODO: Would it be smoother if there was two lists one it goes through then a second that it adds to while first is in progress
            running = true;
            hasPlayers = !Bukkit.getOnlinePlayers().isEmpty();
            ArrayList<Location> toRemove = new ArrayList<>();
            for (Location l : locations.keySet()) {
                if (!running || current == null)
                    break;
                Block blc = l.getBlock();
                Vector lv = l.toVector();
                if (isClassicBlock(lv) && blc.isLiquid()) {
                    toRemove.add(l);
                    continue;
                }
                Material type = null;
                ConcurrentLinkedQueue<Location> queue = locations.get(l);
                if (queue != null) {
                    boolean fromClassic = false;
                    while (!queue.isEmpty()) {
                        Location from = queue.poll();
                        if (isClassicBlock(from.toVector()) && from.getBlock().isLiquid()) {
                            fromClassic = true;
                            type = from.getBlock().getType();
                            break;
                        }
                    }
                    if (!fromClassic) {
                        toRemove.add(l);
                        continue;
                    }
                } else {
                    toRemove.add(l);
                    continue;
                }
                if (type == null) {
                    toRemove.add(l);
                    continue;
                }
                if (!isClassicBlock(lv))
                    addClassicBlock(lv);
                e.setBlock(lv.getBlockX(), lv.getBlockY(), lv.getBlockZ(), type);
                if (hasPlayers) {
                    long xz = (long) l.getChunk().getX() << 32 | l.getChunk().getZ() & 0xFFFFFFFFL;
                    if (!chunks.containsKey(xz))
                        chunks.put(xz, new WorldCount(xz));
                    chunks.get(xz).addChange(lv.getBlockX(), lv.getBlockY(), lv.getBlockZ());
                }
                if (!blc.isLiquid())// && !isFusionBlock(lv))
                    e.setBlock(lv.getBlockX(), lv.getBlockY(), lv.getBlockZ(), type = Material.STATIONARY_WATER);
                Bukkit.getPluginManager().callEvent(new ClassicBlockPlaceEvent(l));
                if (running && current != null)
                    for (LogicContainerHolder holder : logicContainers)
                        if (holder.container.doesHandle(type)) {
                            holder.container.queueBlock(l);
                            break;
                        }
                toRemove.add(l);
            }
            if (current == null) {
                running = false;
                return;
            }
            toRemove.forEach(locations::remove);
            running = false;
            if (sendingPackets || !hasPlayers)
                return;
            sendingPackets = true;
            ArrayList<Packet> packets = new ArrayList<>();
            if (!Bukkit.getOnlinePlayers().isEmpty()) //Should this check this or use the estimate of hasPlayers
                for (long l : chunks.keySet()) {
                    if (!sendingPackets)
                        break;
                    packets.addAll(chunks.get(l).getPackets());
                    chunks.remove(l);
                }
            else
                chunks.clear();
            if (!packets.isEmpty()) //TODO figure out why sometimes the packet does not get sent. Is it some over max amount?
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!sendingPackets)
                        break;
                    if (p != null) {
                        final EntityPlayer ep = ((CraftPlayer) p).getHandle();
                        for (Packet packet : packets) {
                            if (!sendingPackets)//Check again in case player is mid getting sent
                                break;
                            ep.playerConnection.sendPacket(packet);
                        }
                    }
                }
            sendingPackets = false;
        }
    };

    public ClassicPhysicsHandler(Plugin plugin) {
        this.owner = plugin;
        addLogicContainer(new LavaLogic());
        addLogicContainer(new WaterLogic());
    }

    public boolean isClassicBlock(Vector v) {
        return classicBlocks.contains(v.toBlockVector());
    }

    public void addClassicBlock(Vector v) { //Make sure that isClassicBlock is called before adding it through here
        classicBlocks.add(v.toBlockVector().clone());
    }

    private void removeClassicBlock(Vector v) {
        classicBlocks.remove(v.toBlockVector());
    }

    //Player placed is stored in classic physics now instead of ls so that we can clear it at the correct time
    public boolean isPlayerPlaced(Vector v) { //If we want to know who placed the block it will need to go to a hash map instead
        return playerPlaced.contains(v.toBlockVector());
    }

    public void addPlayerPlaced(Vector v) { //Make sure that isPlayerPlaced is called before adding it through here
        playerPlaced.add(v.toBlockVector().clone());
    }

    public void removePlayerPlaced(Vector v) {
        playerPlaced.remove(v.toBlockVector());
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

    public void setPhysicsWorld(World w) {
        this.current = w;
        if (w == null) {
            running = false;
            sendingPackets = false;
            this.locations.clear();
            this.chunks.clear();
            classicBlocks.clear();
            playerPlaced.clear();
            logicContainers.forEach(holder -> holder.container.unload());
            e = null;
        } else
            e = new ChunkEdit(((CraftWorld) w).getHandle());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        if (!event.isCancelled() && event.getWorld().equals(this.current))
            setPhysicsWorld(null);
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
        removeClassicBlock(event.getBlock().getLocation().toVector());
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        hasPlayers = true;
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        if (ClassicPhysics.TYPE.equals(PhysicsType.DEFAULT))
            return;
        event.setCancelled(true);
        if (isClassicBlock(event.getToBlock().getLocation().toVector()) && isClassicBlock(event.getBlock().getLocation().toVector()))//To is really the from block it is labelled strangely
            placeClassicBlockAt(event.getBlock().getLocation(), event.getToBlock().getType(), event.getToBlock().getLocation());
    }

    private void requestUpdateAround(Location location) {
        if (current == null || location == null || !location.getWorld().equals(current))
            return;
        try {
            if (!location.getChunk().isLoaded()) //Chunk is not loaded
                return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    Location newLoc = location.clone().add(x, y, z);
                    if (!isClassicBlock(newLoc.toVector()))
                        continue;
                    for (LogicContainerHolder holder : logicContainers)
                        if (holder.container.doesHandle(newLoc.getBlock().getType())) {
                            holder.container.queueBlock(newLoc);
                            break;
                        }
                }
    }

    public void forcePlaceClassicBlockAt(Location location, Material type) {//Force place block
        if (current == null || location == null || !location.getWorld().equals(current))
            return;
        try {
            if (!location.getChunk().isLoaded())//Chunk isn't loaded
                return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (type.equals(Material.WATER))
            type = Material.STATIONARY_WATER;
        else if (type.equals(Material.LAVA))
            type = Material.STATIONARY_LAVA;
        Block blc = location.getBlock();
        Vector bv = blc.getLocation().toVector();
        if (!isClassicBlock(bv))
            addClassicBlock(bv);
        e.setBlock(blc.getX(), blc.getY(), blc.getZ(), type);
        if (!blc.isLiquid()) {// && !isFusionBlock(bv)) {
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
                return; //TODO see how many of these checks are non necessary
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (!location.getWorld().equals(current))
            return;
        location = location.getBlock().getLocation(); //make the x,y,z all ints
        if (type.equals(Material.WATER))
            type = Material.STATIONARY_WATER;
        else if (type.equals(Material.LAVA))
            type = Material.STATIONARY_LAVA;
        for (LogicContainerHolder holder : logicContainers)
            if (holder.container.doesHandle(type)) {
                ConcurrentLinkedQueue<Location> temp = locations.get(location);
                if (temp == null)
                    locations.put(location, temp = new ConcurrentLinkedQueue<>());
                    //temp = new ConcurrentLinkedQueue<>();
                temp.offer(from.getBlock().getLocation());
                //locations.put(location, temp);
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